package com.raremarket.backend.service;

import com.raremarket.backend.dto.chat.ConversationResponse;
import com.raremarket.backend.dto.chat.CreateConversationRequest;
import com.raremarket.backend.dto.chat.MessageResponse;
import com.raremarket.backend.dto.chat.SendMessageRequest;
import com.raremarket.backend.model.Conversation;
import com.raremarket.backend.model.Message;
import com.raremarket.backend.repository.ConversationRepository;
import com.raremarket.backend.repository.MessageRepository;
import com.raremarket.backend.repository.OrderConversationRepository;
import com.raremarket.backend.repository.UserRepository;
import com.raremarket.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService Tests")
class ChatServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderConversationRepository orderConversationRepository;

    private ChatService chatService;

    private User buyerUser;
    private User sellerUser;
    private UUID buyerId;
    private UUID sellerId;
    private UUID conversationId;
    private String itemId;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(
                conversationRepository,
                messageRepository,
                userRepository,
                orderConversationRepository
        );

        buyerId = UUID.randomUUID();
        sellerId = UUID.randomUUID();
        conversationId = UUID.randomUUID();
        itemId = "item-123";

        buyerUser = new User();
        buyerUser.setId(buyerId.toString());
        buyerUser.setUsername("buyer");
        buyerUser.setEmail("buyer@example.com");

        sellerUser = new User();
        sellerUser.setId(sellerId.toString());
        sellerUser.setUsername("seller");
        sellerUser.setEmail("seller@example.com");
    }

    // ==================== CREATE OR GET CONVERSATION TESTS ====================

    @Test
    @DisplayName("Should create new conversation successfully")
    void testCreateOrGetConversation_CreateNew() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(sellerId);
        request.setItemId(itemId);

        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(userRepository.existsById(sellerId.toString())).thenReturn(true);
        when(conversationRepository.findByItemIdAndBuyerIdAndSellerId(itemId, buyerId.toString(), sellerId.toString()))
                .thenReturn(Optional.empty());

        Conversation savedConversation = new Conversation();
        savedConversation.setId(conversationId);
        savedConversation.setItemId(itemId);
        savedConversation.setBuyerId(buyerId.toString());
        savedConversation.setSellerId(sellerId.toString());

        when(conversationRepository.save(any(Conversation.class))).thenReturn(savedConversation);
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(buyerUser, sellerUser));
        when(messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversationId)).thenReturn(Optional.empty());
        when(messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(any(), any())).thenReturn(0L);

        // Act
        ConversationResponse result = chatService.createOrGetConversation(request);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should get existing conversation")
    void testCreateOrGetConversation_GetExisting() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(sellerId);
        request.setItemId(itemId);

        Conversation existingConversation = new Conversation();
        existingConversation.setId(conversationId);
        existingConversation.setItemId(itemId);
        existingConversation.setBuyerId(buyerId.toString());
        existingConversation.setSellerId(sellerId.toString());

        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(userRepository.existsById(sellerId.toString())).thenReturn(true);
        when(conversationRepository.findByItemIdAndBuyerIdAndSellerId(itemId, buyerId.toString(), sellerId.toString()))
                .thenReturn(Optional.of(existingConversation));
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(buyerUser, sellerUser));
        when(messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversationId)).thenReturn(Optional.empty());
        when(messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(any(), any())).thenReturn(0L);

        // Act
        ConversationResponse result = chatService.createOrGetConversation(request);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        verify(conversationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail when buyerId is null")
    void testCreateOrGetConversation_NullBuyerId() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(null);
        request.setSellerId(sellerId);
        request.setItemId(itemId);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.createOrGetConversation(request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should fail when sellerId is null")
    void testCreateOrGetConversation_NullSellerId() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(null);
        request.setItemId(itemId);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.createOrGetConversation(request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should fail when buyerId equals sellerId")
    void testCreateOrGetConversation_SameIds() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(buyerId); // Same as buyerId
        request.setItemId(itemId);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.createOrGetConversation(request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("buyerId and sellerId must be different", exception.getReason());
    }

    @Test
    @DisplayName("Should fail when itemId is blank")
    void testCreateOrGetConversation_BlankItemId() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(sellerId);
        request.setItemId("   ");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.createOrGetConversation(request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should fail when buyer user doesn't exist")
    void testCreateOrGetConversation_BuyerNotFound() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setBuyerId(buyerId);
        request.setSellerId(sellerId);
        request.setItemId(itemId);

        when(userRepository.existsById(buyerId.toString())).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.createOrGetConversation(request)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // ==================== LIST CONVERSATIONS TESTS ====================

    @Test
    @DisplayName("Should list all conversations for user")
    void testListConversations_Success() {
        // Arrange
        when(userRepository.existsById(buyerId.toString())).thenReturn(true);

        Conversation conversation1 = new Conversation();
        conversation1.setId(conversationId);
        conversation1.setBuyerId(buyerId.toString());
        conversation1.setSellerId(sellerId.toString());
        conversation1.setItemId(itemId);

        when(conversationRepository.findByBuyerIdOrSellerIdOrderByUpdatedAtDesc(buyerId.toString(), buyerId.toString()))
                .thenReturn(List.of(conversation1));
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(buyerUser, sellerUser));
        when(messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversationId)).thenReturn(Optional.empty());
        when(messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(any(), any())).thenReturn(0L);

        // Act
        List<ConversationResponse> result = chatService.listConversations(buyerId);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should fail listing conversations when user not found")
    void testListConversations_UserNotFound() {
        // Arrange
        when(userRepository.existsById(buyerId.toString())).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.listConversations(buyerId)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // ==================== SEND MESSAGE TESTS ====================

    @Test
    @DisplayName("Should send message successfully")
    void testSendMessage_Success() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setSenderId(buyerId);
        request.setContent("Hello, are you interested?");

        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setBuyerId(buyerId.toString());
        conversation.setSellerId(sellerId.toString());

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setConversationId(conversationId);
        message.setSenderId(buyerId.toString());
        message.setContent("Hello, are you interested?");
        message.setRead(false);

        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(buyerUser, sellerUser));

        // Act
        MessageResponse result = chatService.sendMessage(conversationId, request);

        // Assert
        assertNotNull(result);
        assertEquals("Hello, are you interested?", result.getContent());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("Should fail sending message with null senderId")
    void testSendMessage_NullSenderId() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setSenderId(null);
        request.setContent("Hello");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.sendMessage(conversationId, request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("senderId is required", exception.getReason());
    }

    @Test
    @DisplayName("Should fail sending message with blank content")
    void testSendMessage_BlankContent() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setSenderId(buyerId);
        request.setContent("   ");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.sendMessage(conversationId, request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("content is required", exception.getReason());
    }

    @Test
    @DisplayName("Should trim message content")
    void testSendMessage_TrimContent() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setSenderId(buyerId);
        request.setContent("  Hello  ");

        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setBuyerId(buyerId.toString());
        conversation.setSellerId(sellerId.toString());

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setConversationId(conversationId);
        message.setSenderId(buyerId.toString());
        message.setContent("Hello");

        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(buyerUser, sellerUser));

        // Act
        chatService.sendMessage(conversationId, request);

        // Assert
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        assertEquals("Hello", messageCaptor.getValue().getContent());
    }

    // ==================== MARK CONVERSATION AS READ TESTS ====================

    @Test
    @DisplayName("Should mark conversation as read")
    void testMarkConversationAsRead_Success() {
        // Arrange
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setBuyerId(buyerId.toString());
        conversation.setSellerId(sellerId.toString());

        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(messageRepository.markConversationAsRead(conversationId, buyerId.toString())).thenReturn(5);

        // Act
        int result = chatService.markConversationAsRead(conversationId, buyerId);

        // Assert
        assertEquals(5, result);
        verify(messageRepository).markConversationAsRead(conversationId, buyerId.toString());
    }

    @Test
    @DisplayName("Should fail marking as read when conversation not found")
    void testMarkConversationAsRead_ConversationNotFound() {
        // Arrange
        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.markConversationAsRead(conversationId, buyerId)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // ==================== DELETE CONVERSATION TESTS ====================

    @Test
    @DisplayName("Should delete conversation successfully")
    void testDeleteConversation_Success() {
        // Arrange
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setBuyerId(buyerId.toString());
        conversation.setSellerId(sellerId.toString());

        when(userRepository.existsById(buyerId.toString())).thenReturn(true);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

        // Act
        chatService.deleteConversation(conversationId, buyerId);

        // Assert
        verify(messageRepository).deleteByConversationId(conversationId);
        verify(conversationRepository).deleteById(conversationId);
    }

    @Test
    @DisplayName("Should fail deleting conversation when not authorized")
    void testDeleteConversation_NotAuthorized() {
        // Arrange
        UUID unauthorizedUserId = UUID.randomUUID();
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setBuyerId(buyerId.toString());
        conversation.setSellerId(sellerId.toString());

        when(userRepository.existsById(unauthorizedUserId.toString())).thenReturn(true);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chatService.deleteConversation(conversationId, unauthorizedUserId)
        );
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}
