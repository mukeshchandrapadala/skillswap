package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dto.*;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.service.AuthService;
import com.skillswap.skillswap.service.NotificationService;
import com.skillswap.skillswap.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final NotificationService notificationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", resp));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me() {
        User user = authService.getProfile(AuthUtil.currentUserId());
        return ResponseEntity.ok(ApiResponse.ok(null, user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UpdateProfileRequest req) {
        User updated = authService.updateProfile(AuthUtil.currentUserId(), req);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", updated));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(@RequestParam(defaultValue = "") String q,
                                               @RequestParam(required = false) String language,
                                               @RequestParam(required = false) String location,
                                               @RequestParam(required = false) String skill) {
        List<User> users = authService.search(q, language, location, skill);
        return ResponseEntity.ok(ApiResponse.ok(null, users));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        User user = authService.getProfile(id);
        user.setOnline(notificationService.isOnline(id));
        return ResponseEntity.ok(ApiResponse.ok(null, user));
    }

    // Public profile — no auth required, used for shareable profile links
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse> publicProfile(@PathVariable Long id) {
        User user = authService.getProfile(id);
        user.setOnline(notificationService.isOnline(id));
        return ResponseEntity.ok(ApiResponse.ok(null, user));
    }

    @GetMapping("/online-status")
    public ResponseEntity<ApiResponse> onlineStatus(@RequestParam List<Long> ids) {
        Map<Long, Boolean> status = new HashMap<>();
        ids.forEach(id -> status.put(id, notificationService.isOnline(id)));
        return ResponseEntity.ok(ApiResponse.ok(null, status));
    }
}
