package com.skillswap.skillswap.service;

import com.skillswap.skillswap.model.*;
import com.skillswap.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillRequestService {

    private final SkillRequestRepository repo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public SkillRequest send(Long senderId, Long receiverId, String skillOffered, String skillWanted) {
        if (senderId.equals(receiverId))
            throw new RuntimeException("Cannot send a swap request to yourself");

        User sender   = userRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepo.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        SkillRequest sr = SkillRequest.builder()
                .sender(sender).receiver(receiver)
                .skillOffered(skillOffered).skillWanted(skillWanted)
                .status(RequestStatus.PENDING).build();

        SkillRequest saved = repo.save(sr);

        // real-time notification to receiver
        notificationService.send(receiverId, "swap_request",
                java.util.Map.of("from", sender.getName(), "skillOffered", skillOffered));

        return saved;
    }

    public List<SkillRequest> incoming(Long userId) {
        return repo.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    public List<SkillRequest> sent(Long userId) {
        return repo.findBySenderIdOrderByCreatedAtDesc(userId);
    }

    public SkillRequest updateStatus(Long requestId, RequestStatus status) {
        SkillRequest sr = repo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        sr.setStatus(status);
        SkillRequest updated = repo.save(sr);

        // notify sender
        notificationService.send(sr.getSender().getId(), "swap_update",
                java.util.Map.of("status", status, "skillOffered", sr.getSkillOffered()));

        return updated;
    }

    public long pendingCount(Long userId) {
        return repo.countByReceiverIdAndStatus(userId, RequestStatus.PENDING);
    }
}
