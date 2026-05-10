package com.raremarket.backend.service;

import com.raremarket.backend.dto.order.OrderCreateRequest;
import com.raremarket.backend.dto.order.OrderResponse;
import com.raremarket.backend.dto.order.OrderStatusUpdateRequest;
import com.raremarket.backend.model.Item;
import com.raremarket.backend.model.Order;
import com.raremarket.backend.repository.ItemRepository;
import com.raremarket.backend.repository.OrderRepository;
import com.raremarket.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class OrderService {
    private static final String DELIVERY_SHIPPING = "shipping";

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            ItemRepository itemRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(String authenticatedBuyerId, OrderCreateRequest request) {
        ensureUserExists(authenticatedBuyerId);

        Item item = itemRepository.findById(request.getItemId().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!item.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item already sold");
        }

        if (item.getSellerId() == null || item.getSellerId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item seller not found");
        }

        if (item.getSellerId().equals(authenticatedBuyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot buy your own item");
        }

        String deliveryMethod = normalizeDeliveryMethod(request.getDeliveryMethod());

        Order order = new Order();
        order.setItemId(item.getId());
        order.setBuyerId(authenticatedBuyerId);
        order.setSellerId(item.getSellerId());
        order.setItemTitle(item.getTitulo());
        order.setItemImage(item.getImagen());
        order.setAmountEur(item.getPrecioEur());
        order.setDeliveryMethod(deliveryMethod);

        String cardDigits = sanitizeCardNumber(request.getCardNumber());
        if (cardDigits.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid card number");
        }
        order.setPaymentBrand(detectCardBrand(cardDigits));
        order.setPaymentLast4(cardDigits.substring(cardDigits.length() - 4));
        order.setStatus("PREPARANDO_ENVIO");

        applyDeliveryData(order, request);

        Order savedOrder = orderRepository.save(order);

        item.setAvailable(false);
        itemRepository.save(item);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listMyOrders(String authenticatedUserId, String role) {
        ensureUserExists(authenticatedUserId);
        boolean asSeller = role != null && role.trim().equalsIgnoreCase("seller");

        List<Order> orders = asSeller
                ? orderRepository.findBySellerIdOrderByCreatedAtDesc(authenticatedUserId)
                : orderRepository.findByBuyerIdOrderByCreatedAtDesc(authenticatedUserId);

        return orders.stream().map(OrderResponse::from).toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(String orderId, String authenticatedUserId, OrderStatusUpdateRequest request) {
        ensureUserExists(authenticatedUserId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String targetStatus = normalizeStatus(request.getStatus());
        String currentStatus = normalizeStatus(order.getStatus());

        boolean isSeller = authenticatedUserId.equals(order.getSellerId());
        boolean isBuyer = authenticatedUserId.equals(order.getBuyerId());

        if (!isSeller && !isBuyer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this order");
        }

        boolean allowed = isSeller
            ? canSellerTransition(currentStatus, targetStatus)
                : canBuyerTransition(currentStatus, targetStatus);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status transition");
        }

        order.setStatus(targetStatus);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    private void applyDeliveryData(Order order, OrderCreateRequest request) {
        order.setShippingFullName(requireText(request.getShippingFullName(), "shippingFullName"));
        order.setShippingPhone(requireText(request.getShippingPhone(), "shippingPhone"));
        order.setShippingAddressLine1(requireText(request.getShippingAddressLine1(), "shippingAddressLine1"));
        order.setShippingCity(requireText(request.getShippingCity(), "shippingCity"));
        order.setShippingPostalCode(requireText(request.getShippingPostalCode(), "shippingPostalCode"));
        order.setShippingCountry(requireText(request.getShippingCountry(), "shippingCountry"));
        order.setPickupCity(null);
        order.setPickupNotes(null);
    }

    private String normalizeDeliveryMethod(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deliveryMethod is required");
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (!DELIVERY_SHIPPING.equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deliveryMethod must be shipping");
        }

        return normalized;
    }

    private String normalizeStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        return rawStatus.trim().toUpperCase(Locale.ROOT);
    }

    private boolean canSellerTransition(String from, String to) {
        if ("PREPARANDO_ENVIO".equals(from)) {
            return "ENVIADO".equals(to);
        }

        if ("ENVIADO".equals(from)) {
            return "ENTREGADO".equals(to);
        }

        return false;
    }

    private boolean canBuyerTransition(String from, String to) {
        if (("PREPARANDO_ENVIO".equals(from) || "ACEPTADO".equals(from)) && "CANCELADO".equals(to)) {
            return true;
        }

        if (("ENVIADO".equals(from) || "ENTREGADO".equals(from)) && "COMPLETADO".equals(to)) {
            return true;
        }

        return false;
    }

    private String sanitizeCardNumber(String rawCardNumber) {
        if (rawCardNumber == null) {
            return "";
        }

        StringBuilder digits = new StringBuilder();
        for (char ch : rawCardNumber.toCharArray()) {
            if (Character.isDigit(ch)) {
                digits.append(ch);
            }
        }
        return digits.toString();
    }

    private String detectCardBrand(String cardDigits) {
        if (cardDigits.startsWith("4")) {
            return "VISA";
        }
        if (cardDigits.startsWith("5") || cardDigits.startsWith("2")) {
            return "MASTERCARD";
        }
        return "CARD";
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        return value.trim();
    }

    private void ensureUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
