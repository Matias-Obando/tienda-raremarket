package com.raremarket.backend.repository;

import com.raremarket.backend.model.OrderConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderConversationRepository extends JpaRepository<OrderConversation, UUID> {
    Optional<OrderConversation> findByOrderId(String orderId);
}
