package com.raremarket.backend.repository;

import com.raremarket.backend.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, String> {
  @Query(value = """
    select
      m.id::text,
      m.conversation_id::text,
      m.sender_id::text,
      m.content,
      m.is_read,
      m.created_at
    from messages m
    where m.conversation_id::text = :conversationId
    order by m.created_at asc
    """, nativeQuery = true)
  List<Object[]> findMessagesRawByConversationId(@Param("conversationId") String conversationId);

    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    long countByConversationIdAndSenderIdNotAndIsReadFalse(UUID conversationId, String senderId);

    long deleteByConversationIdIn(Collection<UUID> conversationIds);

    long deleteByConversationId(UUID conversationId);

    @Transactional
    @Modifying
    @Query(value = """
      delete from messages
      where conversation_id::text = :conversationId
      """, nativeQuery = true)
    int deleteByConversationIdRaw(@Param("conversationId") String conversationId);

    @Transactional
    @Modifying
    @Query("""
        update Message m
        set m.isRead = true
        where m.conversationId = :conversationId
          and m.senderId <> :userId
          and m.isRead = false
        """)
    int markConversationAsRead(@Param("conversationId") UUID conversationId, @Param("userId") String userId);
}
