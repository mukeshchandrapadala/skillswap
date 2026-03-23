package com.skillswap.skillswap.service;

import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    public SseEmitter subscribe(Long userId) {
        // Mark user online
        userRepository.findById(userId).ifPresent(u -> {
            u.setOnline(true);
            u.setLastSeen(LocalDateTime.now());
            userRepository.save(u);
        });

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        Runnable cleanup = () -> {
            emitters.remove(userId);
            markOffline(userId);
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException ignored) {}

        return emitter;
    }

    public void markOffline(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setOnline(false);
            u.setLastSeen(LocalDateTime.now());
            userRepository.save(u);
        });
    }

    public void send(Long userId, String type, Object payload) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(type).data(payload));
            } catch (IOException e) {
                emitters.remove(userId);
                markOffline(userId);
            }
        }
    }

    public boolean isOnline(Long userId) {
        return emitters.containsKey(userId);
    }
}
