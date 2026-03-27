package com.raremarket.backend.repository;

import com.raremarket.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByItemIdAndBuyerIdAndSellerId(String itemId, UUID buyerId, UUID sellerId);

    List<Conversation> findByBuyerIdOrSellerIdOrderByUpdatedAtDesc(UUID buyerId, UUID sellerId);
}
