package com.skillswap.skillswap.service;

import com.skillswap.skillswap.model.ChatMessage;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.ChatMessageRepository;
import com.skillswap.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public ChatMessage send(Long senderId, Long receiverId, String content) {
        User sender   = userRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepo.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage msg = ChatMessage.builder()
                .sender(sender).receiver(receiver)
                .content(content).read(false).build();

        ChatMessage saved = chatRepo.save(msg);

        notificationService.send(receiverId, "chat_message",
                Map.of(
                    "id",      saved.getId(),
                    "from",    sender.getName(),
                    "fromId",  senderId,
                    "content", content,
                    "time",    saved.getCreatedAt().toString()
                ));

        return saved;
    }

    public List<ChatMessage> conversation(Long userA, Long userB) {
        List<ChatMessage> msgs = chatRepo.findConversation(userA, userB);
        // mark incoming messages as read
        msgs.stream()
            .filter(m -> m.getReceiver().getId().equals(userA) && !m.isRead())
            .forEach(m -> { m.setRead(true); chatRepo.save(m); });
        return chatRepo.findConversation(userA, userB);
    }

    public List<User> partners(Long userId) {
        List<ChatMessage> all = chatRepo.findAllByUserId(userId);
        // Collect unique partners preserving most-recent-first order
        Map<Long, User> seen = new LinkedHashMap<>();
        for (ChatMessage m : all) {
            User other = m.getSender().getId().equals(userId) ? m.getReceiver() : m.getSender();
            seen.putIfAbsent(other.getId(), other);
        }
        return new ArrayList<>(seen.values());
    }

    public long unreadCount(Long senderId, Long receiverId) {
        return chatRepo.countBySenderIdAndReceiverIdAndReadFalse(senderId, receiverId);
    }
}
