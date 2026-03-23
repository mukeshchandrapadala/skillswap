package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.SkillRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRequestRepository extends JpaRepository<SkillRequest, Long> {
    List<SkillRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<SkillRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    long countByReceiverIdAndStatus(Long receiverId, com.skillswap.skillswap.model.RequestStatus status);
}
