package com.raremarket.backend.repository;

import com.raremarket.backend.model.OrderConversation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderConversationRepository extends JpaRepository<OrderConversation, UUID> {
    Optional<OrderConversation> findByOrderId(String orderId);

    long deleteByConversationIdIn(Collection<UUID> conversationIds);

    @Transactional
    @Modifying
    @Query(value = """
        delete from order_conversations
        where conversation_id::text = :conversationId
        """, nativeQuery = true)
    int deleteByConversationIdRaw(@Param("conversationId") String conversationId);
}
