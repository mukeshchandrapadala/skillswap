package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dto.ApiResponse;
import com.skillswap.skillswap.model.ChatMessage;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.service.ChatService;
import com.skillswap.skillswap.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> send(
            @RequestParam Long receiverId,
            @RequestBody Map<String, String> body) {
        ChatMessage msg = chatService.send(AuthUtil.currentUserId(), receiverId, body.get("content"));
        return ResponseEntity.ok(ApiResponse.ok(null, msg));
    }

    @GetMapping("/conversation/{partnerId}")
    public ResponseEntity<ApiResponse> conversation(@PathVariable Long partnerId) {
        List<ChatMessage> msgs = chatService.conversation(AuthUtil.currentUserId(), partnerId);
        return ResponseEntity.ok(ApiResponse.ok(null, msgs));
    }

    @GetMapping("/partners")
    public ResponseEntity<ApiResponse> partners() {
        List<User> partners = chatService.partners(AuthUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null, partners));
    }

    @GetMapping("/unread/{partnerId}")
    public ResponseEntity<ApiResponse> unread(@PathVariable Long partnerId) {
        long count = chatService.unreadCount(partnerId, AuthUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null, count));
    }
}
