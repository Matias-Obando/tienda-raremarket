package com.raremarket.backend.controller;

import com.raremarket.backend.dto.chat.ConversationResponse;
import com.raremarket.backend.dto.chat.CreateConversationRequest;
import com.raremarket.backend.dto.chat.MessageResponse;
import com.raremarket.backend.dto.chat.SendMessageRequest;
import com.raremarket.backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations")
    public ConversationResponse createConversation(@RequestBody CreateConversationRequest request) {
        return chatService.createOrGetConversation(request);
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> listConversations(@RequestParam UUID userId) {
        return chatService.listConversations(userId);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageResponse> listMessages(@PathVariable UUID conversationId, @RequestParam UUID userId) {
        return chatService.listMessages(conversationId, userId);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public MessageResponse sendMessage(@PathVariable UUID conversationId, @RequestBody SendMessageRequest request) {
        return chatService.sendMessage(conversationId, request);
    }

    @PatchMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Map<String, Integer>> markAsRead(@PathVariable UUID conversationId, @RequestParam UUID userId) {
        int updated = chatService.markConversationAsRead(conversationId, userId);
        return ResponseEntity.ok(Map.of("updated", updated));
    }
}
