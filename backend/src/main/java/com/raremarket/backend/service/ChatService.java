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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {
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

        Conversation conversation = conversationRepository
                .findByItemIdAndBuyerIdAndSellerId(request.getItemId().trim(), request.getBuyerId(), request.getSellerId())
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setItemId(request.getItemId().trim());
                    newConversation.setBuyerId(request.getBuyerId());
                    newConversation.setSellerId(request.getSellerId());
                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation, request.getBuyerId());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> listConversations(UUID userId) {
        ensureUserExists(userId);
        return conversationRepository.findByBuyerIdOrSellerIdOrderByUpdatedAtDesc(userId, userId).stream()
                .map(conversation -> toConversationResponse(conversation, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listMessages(UUID conversationId, UUID userId) {
        Conversation conversation = getAuthorizedConversation(conversationId, userId);
        Map<UUID, User> usersById = loadUsers(List.of(conversation.getBuyerId(), conversation.getSellerId()));

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(message -> toMessageResponse(message, usersById))
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
        message.setSenderId(request.getSenderId());
        message.setContent(content);
        message.setRead(false);

        Message savedMessage = messageRepository.save(message);
        Map<UUID, User> usersById = loadUsers(List.of(conversation.getBuyerId(), conversation.getSellerId()));
        return toMessageResponse(savedMessage, usersById);
    }

    @Transactional
    public int markConversationAsRead(UUID conversationId, UUID userId) {
        getAuthorizedConversation(conversationId, userId);
        return messageRepository.markConversationAsRead(conversationId, userId);
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

        boolean isParticipant = userId.equals(conversation.getBuyerId()) || userId.equals(conversation.getSellerId());
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

    private Map<UUID, User> loadUsers(Collection<UUID> ids) {
        List<String> userIds = ids.stream().map(UUID::toString).toList();
        Map<UUID, User> usersById = new HashMap<>();

        for (User user : userRepository.findAllById(userIds)) {
            try {
                usersById.put(UUID.fromString(user.getId()), user);
            } catch (IllegalArgumentException ignored) {
                // Ignora ids legacy no-UUID para no romper el flujo de chat.
            }
        }

        return usersById;
    }

    private ConversationResponse toConversationResponse(Conversation conversation, UUID currentUserId) {
        Map<UUID, User> usersById = loadUsers(List.of(conversation.getBuyerId(), conversation.getSellerId()));
        User buyer = usersById.get(conversation.getBuyerId());
        User seller = usersById.get(conversation.getSellerId());
        User counterpart = currentUserId.equals(conversation.getBuyerId()) ? seller : buyer;
        UUID counterpartId = currentUserId.equals(conversation.getBuyerId()) ? conversation.getSellerId() : conversation.getBuyerId();
        Optional<Message> lastMessage = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId());

        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setItemId(conversation.getItemId());
        response.setBuyerId(conversation.getBuyerId());
        response.setBuyerName(buyer != null ? buyer.getUsername() : "Usuario");
        response.setSellerId(conversation.getSellerId());
        response.setSellerName(seller != null ? seller.getUsername() : "Usuario");
        response.setCounterpartId(counterpartId);
        response.setCounterpartName(counterpart != null ? counterpart.getUsername() : "Usuario");
        response.setUnreadCount(messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(conversation.getId(), currentUserId));
        response.setUpdatedAt(conversation.getUpdatedAt());

        lastMessage.ifPresent(message -> {
            response.setLastMessage(message.getContent());
            response.setLastMessageAt(message.getCreatedAt());
        });

        return response;
    }

    private MessageResponse toMessageResponse(Message message, Map<UUID, User> usersById) {
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
        conversation.setBuyerId(buyerId);
        conversation.setSellerId(sellerId);
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
        message.setSenderId(buyerId); // El comprador inicia el mensaje (representa el sistema)
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
