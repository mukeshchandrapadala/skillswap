package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.service.NotificationService;
import com.skillswap.skillswap.util.AuthUtil;
import com.skillswap.skillswap.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam String token) {
        if (!jwtUtil.isValid(token)) throw new RuntimeException("Invalid token");
        Long userId = jwtUtil.extractUserId(token);
        return notificationService.subscribe(userId);
    }

    // Typing indicator — called when user is typing in a chat
    @PostMapping("/typing")
    public void typing(@RequestParam Long receiverId) {
        Long senderId = AuthUtil.currentUserId();
        notificationService.send(receiverId, "typing",
            Map.of("fromId", senderId));
    }
}
