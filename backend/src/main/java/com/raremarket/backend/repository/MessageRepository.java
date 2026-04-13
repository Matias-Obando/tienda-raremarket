package com.raremarket.backend.repository;

import com.raremarket.backend.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    long countByConversationIdAndSenderIdNotAndIsReadFalse(UUID conversationId, UUID senderId);

    @Transactional
    @Modifying
    @Query("""
        update Message m
        set m.isRead = true
        where m.conversationId = :conversationId
          and m.senderId <> :userId
          and m.isRead = false
        """)
    int markConversationAsRead(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
}
