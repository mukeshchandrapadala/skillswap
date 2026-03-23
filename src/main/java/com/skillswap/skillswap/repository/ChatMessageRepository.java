package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.sender.id = :a AND m.receiver.id = :b)
           OR (m.sender.id = :b AND m.receiver.id = :a)
        ORDER BY m.createdAt ASC
    """)
    List<ChatMessage> findConversation(@Param("a") Long a, @Param("b") Long b);

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.sender.id = :userId OR m.receiver.id = :userId
        ORDER BY m.createdAt DESC
    """)
    List<ChatMessage> findAllByUserId(@Param("userId") Long userId);

    // use 'read' as the Java field name (maps to is_read column)
    long countBySenderIdAndReceiverIdAndReadFalse(Long senderId, Long receiverId);
}
