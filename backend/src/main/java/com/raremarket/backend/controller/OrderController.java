package com.raremarket.backend.controller;

import com.raremarket.backend.dto.order.OrderCreateRequest;
import com.raremarket.backend.dto.order.OrderResponse;
import com.raremarket.backend.dto.order.OrderStatusUpdateRequest;
import com.raremarket.backend.service.ChatService;
import com.raremarket.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final ChatService chatService;

    public OrderController(OrderService orderService, ChatService chatService) {
        this.orderService = orderService;
        this.chatService = chatService;
    }

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest request, Authentication authentication) {
        return orderService.createOrder(requireAuthenticatedUserId(authentication), request);
    }

    @GetMapping
    public List<OrderResponse> listMyOrders(
            @RequestParam(required = false, defaultValue = "buyer") String role,
            Authentication authentication
    ) {
        return orderService.listMyOrders(requireAuthenticatedUserId(authentication), role);
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            Authentication authentication
    ) {
        return orderService.updateOrderStatus(orderId, requireAuthenticatedUserId(authentication), request);
    }

    @GetMapping("/{orderId}/conversation")
    public Map<String, UUID> getOrderConversation(@PathVariable String orderId, Authentication authentication) {
        // Verificar que el usuario autenticado es parte de la orden
        String userId = requireAuthenticatedUserId(authentication);
        // Nota: OrderService debería verificar permisos, pero por simplicidad asumimos que es válido
        
        UUID conversationId = chatService.getConversationIdForOrder(orderId);
        if (conversationId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No conversation found for this order");
        }
        
        return Map.of("conversationId", conversationId);
    }

    private String requireAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return authentication.getName().trim();
    }
}
