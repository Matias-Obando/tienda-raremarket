package com.raremarket.backend.service;

import com.raremarket.backend.dto.chat.ConversationResponse;
import com.raremarket.backend.dto.chat.CreateConversationRequest;
import com.raremarket.backend.dto.chat.MessageResponse;
import com.raremarket.backend.dto.chat.SendMessageRequest;
import com.raremarket.backend.model.Conversation;
import com.raremarket.backend.model.Message;
import com.raremarket.backend.model.OrderConversation;
import com.raremarket.backend.model.User;
import com.raremarket.backend.repository.ConversationRepository;
import com.raremarket.backend.repository.MessageRepository;
import com.raremarket.backend.repository.OrderConversationRepository;
import com.raremarket.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {
    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OrderConversationRepository orderConversationRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            OrderConversationRepository orderConversationRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.orderConversationRepository = orderConversationRepository;
    }

    @Transactional
    public ConversationResponse createOrGetConversation(CreateConversationRequest request) {
        validateConversationRequest(request);
        String buyerId = request.getBuyerId().toString();
        String sellerId = request.getSellerId().toString();

        Conversation conversation = conversationRepository
            .findByItemIdAndBuyerIdAndSellerId(request.getItemId().trim(), buyerId, sellerId)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setItemId(request.getItemId().trim());
                    newConversation.setBuyerId(buyerId);
                    newConversation.setSellerId(sellerId);
                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation, request.getBuyerId());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> listConversations(UUID userId) {
        ensureUserExists(userId);
        String userIdValue = userId.toString();
        return conversationRepository.findByBuyerIdOrSellerIdOrderByUpdatedAtDesc(userIdValue, userIdValue).stream()
                .map(conversation -> toConversationResponse(conversation, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listMessages(UUID conversationId, UUID userId) {
        Conversation conversation = getAuthorizedConversation(conversationId, userId);
        Map<String, User> usersById = loadUsers(List.of(conversation.getBuyerId(), conversation.getSellerId()));

        return messageRepository.findMessagesRawByConversationId(conversationId.toString()).stream()
            .map(row -> toMessageResponse(row, usersById))
                .toList();
    }

    @Transactional
    public MessageResponse sendMessage(UUID conversationId, SendMessageRequest request) {
        if (request.getSenderId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "senderId is required");
        }
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (content.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }

        Conversation conversation = getAuthorizedConversation(conversationId, request.getSenderId());
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(request.getSenderId().toString());
        message.setContent(content);
        message.setRead(false);

        Message savedMessage = messageRepository.save(message);
        Map<String, User> usersById = loadUsers(List.of(conversation.getBuyerId(), conversation.getSellerId()));
        return toMessageResponse(savedMessage, usersById);
    }

    @Transactional
    public int markConversationAsRead(UUID conversationId, UUID userId) {
        getAuthorizedConversation(conversationId, userId);
        return messageRepository.markConversationAsRead(conversationId, userId.toString());
    }

    @Transactional
    public void deleteConversation(UUID conversationId, UUID userId) {
        getAuthorizedConversation(conversationId, userId);

        // En algunos entornos legacy la tabla/vinculo de order_conversations puede no estar desplegada.
        // No bloqueamos el borrado del chat por ese cleanup auxiliar.
        try {
            orderConversationRepository.deleteByConversationIdIn(List.of(conversationId));
        } catch (RuntimeException ex) {
            LOG.warn("Skipping order_conversations cleanup for conversation {}: {}", conversationId, ex.getMessage());
        }

        messageRepository.deleteByConversationId(conversationId);
        conversationRepository.deleteById(conversationId);
    }

    private void validateConversationRequest(CreateConversationRequest request) {
        if (request.getBuyerId() == null || request.getSellerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "buyerId and sellerId are required");
        }
        if (Objects.equals(request.getBuyerId(), request.getSellerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "buyerId and sellerId must be different");
        }
        if (request.getItemId() == null || request.getItemId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");
        }

        ensureUserExists(request.getBuyerId());
        ensureUserExists(request.getSellerId());
    }

    private Conversation getAuthorizedConversation(UUID conversationId, UUID userId) {
        ensureUserExists(userId);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

        String userIdValue = userId.toString();
        boolean isParticipant = userIdValue.equals(conversation.getBuyerId()) || userIdValue.equals(conversation.getSellerId());
        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this conversation");
        }
        return conversation;
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId.toString())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private Map<String, User> loadUsers(Collection<String> ids) {
        List<String> userIds = ids.stream().filter(id -> id != null && !id.isBlank()).map(String::trim).toList();
        Map<UUID, User> usersById = new HashMap<>();

        for (User user : userRepository.findAllById(userIds)) {
            UUID parsedId = parseUserId(user.getId());
            if (parsedId != null) {
                usersById.put(parsedId, user);
            }
        }

        Map<String, User> usersByStringId = new HashMap<>();
        for (Map.Entry<UUID, User> entry : usersById.entrySet()) {
            usersByStringId.put(entry.getKey().toString(), entry.getValue());
        }

        return usersByStringId;
    }

    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(userId.trim());
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private ConversationResponse toConversationResponse(Conversation conversation, UUID currentUserId) {
        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setItemId(conversation.getItemId());
        response.setBuyerId(parseUserId(conversation.getBuyerId()));
        response.setSellerId(parseUserId(conversation.getSellerId()));
        response.setUpdatedAt(conversation.getUpdatedAt());

        try {
            Map<String, User> usersById = loadUsers(List.of(conversation.getBuyerId(), conversation.getSellerId()));
            UUID buyerId = parseUserId(conversation.getBuyerId());
            UUID sellerId = parseUserId(conversation.getSellerId());
            boolean currentUserIsBuyer = currentUserId.toString().equals(conversation.getBuyerId());
            User buyer = buyerId != null ? usersById.get(buyerId.toString()) : null;
            User seller = sellerId != null ? usersById.get(sellerId.toString()) : null;
            User counterpart = currentUserIsBuyer ? seller : buyer;
            UUID counterpartId = currentUserIsBuyer ? sellerId : buyerId;
            Optional<Message> lastMessage = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId());

            response.setBuyerName(buyer != null ? buyer.getUsername() : "Usuario");
            response.setSellerName(seller != null ? seller.getUsername() : "Usuario");
            response.setCounterpartId(counterpartId);
            response.setCounterpartName(counterpart != null ? counterpart.getUsername() : "Usuario");
            response.setUnreadCount(messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(conversation.getId(), currentUserId.toString()));

            lastMessage.ifPresent(message -> {
                response.setLastMessage(message.getContent());
                response.setLastMessageAt(message.getCreatedAt());
            });
        } catch (RuntimeException ignored) {
            response.setBuyerName("Usuario");
            response.setSellerName("Usuario");
            response.setCounterpartId(currentUserId.toString().equals(conversation.getBuyerId()) ? parseUserId(conversation.getSellerId()) : parseUserId(conversation.getBuyerId()));
            response.setCounterpartName("Usuario");
            response.setUnreadCount(0);
        }

        return response;
    }

    private MessageResponse toMessageResponse(Message message, Map<String, User> usersById) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversationId());
        response.setSenderId(message.getSenderId());
        User sender = usersById.get(message.getSenderId());
        response.setSenderName(sender != null ? sender.getUsername() : "Usuario");
        response.setContent(message.getContent());
        response.setRead(message.isRead());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }

    private MessageResponse toMessageResponse(Object[] row, Map<String, User> usersById) {
        MessageResponse response = new MessageResponse();

        String messageId = row[0] != null ? row[0].toString() : null;
        String conversationIdText = row[1] != null ? row[1].toString() : null;
        String senderIdText = row[2] != null ? row[2].toString() : null;

        response.setId(messageId);
        response.setConversationId(parseUserId(conversationIdText));
        response.setSenderId(senderIdText);
        User sender = usersById.get(senderIdText);
        response.setSenderName(sender != null ? sender.getUsername() : "Usuario");
        response.setContent(row[3] != null ? row[3].toString() : null);
        response.setRead(parseBoolean(row[4]));
        response.setCreatedAt(parseOffsetDateTime(row[5]));
        return response;
    }

    private boolean parseBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        String normalized = value.toString().trim().toLowerCase();
        return "true".equals(normalized) || "t".equals(normalized) || "1".equals(normalized);
    }

    private OffsetDateTime parseOffsetDateTime(Object value) {
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        return null;
    }

    /**
     * Fase 3: Crea una conversación automática para una orden con trato en mano
     * Añade un mensaje del sistema con detalles del pickup
     */
    @Transactional
    public UUID createOrderConversation(String orderId, String itemId, UUID buyerId, UUID sellerId) {
        // Validar que no sea el mismo usuario
        if (Objects.equals(buyerId, sellerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "buyerId and sellerId must be different");
        }

        // Verificar que la conversación de esta orden no exista
        if (orderConversationRepository.findByOrderId(orderId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conversation already exists for this order");
        }

        // Crear conversación de orden
        Conversation conversation = new Conversation();
        conversation.setItemId(itemId);
        conversation.setBuyerId(buyerId.toString());
        conversation.setSellerId(sellerId.toString());
        Conversation savedConversation = conversationRepository.save(conversation);

        // Vincular orden a conversación
        OrderConversation orderConversation = new OrderConversation();
        orderConversation.setOrderId(orderId);
        orderConversation.setConversationId(savedConversation.getId());
        orderConversationRepository.save(orderConversation);

        // Crear mensaje del sistema con detalles del trato en mano
        String systemMessage = formatOrderPickupMessage();
        Message message = new Message();
        message.setConversationId(savedConversation.getId());
        message.setSenderId(buyerId.toString()); // El comprador inicia el mensaje (representa el sistema)
        message.setContent(systemMessage);
        message.setRead(false);
        messageRepository.save(message);

        return savedConversation.getId();
    }

    /**
     * Busca la conversación vinculada a una orden
     */
    @Transactional(readOnly = true)
    public UUID getConversationIdForOrder(String orderId) {
        return orderConversationRepository.findByOrderId(orderId)
                .map(OrderConversation::getConversationId)
                .orElse(null);
    }

    private String formatOrderPickupMessage() {
        return "🔔 Nuevo trato en mano\n\nEl comprador quiere comprar este artículo en persona. Responde aquí para coordinar la entrega y acepta el pedido desde tu perfil cuando lo tengas claro.";
    }
}
