package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dto.ApiResponse;
import com.skillswap.skillswap.model.RequestStatus;
import com.skillswap.skillswap.model.SkillRequest;
import com.skillswap.skillswap.service.SkillRequestService;
import com.skillswap.skillswap.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class SkillRequestController {

    private final SkillRequestService service;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> send(
            @RequestParam Long receiverId,
            @RequestParam String skillOffered,
            @RequestParam String skillWanted) {
        SkillRequest sr = service.send(AuthUtil.currentUserId(), receiverId, skillOffered, skillWanted);
        return ResponseEntity.ok(ApiResponse.ok("Swap request sent", sr));
    }

    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse> incoming() {
        List<SkillRequest> list = service.incoming(AuthUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null, list));
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse> sent() {
        List<SkillRequest> list = service.sent(AuthUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null, list));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse> status(
            @RequestParam Long requestId,
            @RequestParam RequestStatus status) {
        SkillRequest sr = service.updateStatus(requestId, status);
        return ResponseEntity.ok(ApiResponse.ok("Status updated to " + status, sr));
    }

    @GetMapping("/pending-count")
    public ResponseEntity<ApiResponse> pendingCount() {
        long count = service.pendingCount(AuthUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null, count));
    }
}
