package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dto.*;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered: " + req.getEmail());

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .skillsHave(req.getSkillsHave())
                .skillsWant(req.getSkillsWant())
                .languages(req.getLanguages())
                .bio(req.getBio())
                .location(req.getLocation())
                .role("USER")
                .build();

        User saved = userRepository.save(user);
        log.info("Registered: {}", saved.getEmail());
        return saved;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found for: " + req.getEmail()));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid password");

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        log.info("Login: {}", user.getEmail());

        return LoginResponse.builder()
                .id(user.getId()).name(user.getName()).email(user.getEmail())
                .skillsHave(user.getSkillsHave()).skillsWant(user.getSkillsWant())
                .languages(user.getLanguages()).bio(user.getBio())
                .location(user.getLocation()).avatarBase64(user.getAvatarBase64())
                .role(user.getRole()).token(token).build();
    }

    public User updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getName()         != null) user.setName(req.getName());
        if (req.getSkillsHave()   != null) user.setSkillsHave(req.getSkillsHave());
        if (req.getSkillsWant()   != null) user.setSkillsWant(req.getSkillsWant());
        if (req.getLanguages()    != null) user.setLanguages(req.getLanguages());
        if (req.getBio()          != null) user.setBio(req.getBio());
        if (req.getLocation()     != null) user.setLocation(req.getLocation());
        if (req.getAvatarBase64() != null) user.setAvatarBase64(req.getAvatarBase64());

        return userRepository.save(user);
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> search(String query, String language, String location, String skill) {
        // Base search by name, skillsHave, or languages
        List<User> results = userRepository
                .findBySkillsHaveContainingIgnoreCaseOrNameContainingIgnoreCaseOrLanguagesContainingIgnoreCase(
                        query, query, query);

        // Apply optional filters
        if (language != null && !language.isBlank()) {
            String lang = language.toLowerCase();
            results = results.stream()
                    .filter(u -> u.getLanguages() != null &&
                                 u.getLanguages().toLowerCase().contains(lang))
                    .collect(Collectors.toList());
        }
        if (location != null && !location.isBlank()) {
            String loc = location.toLowerCase();
            results = results.stream()
                    .filter(u -> u.getLocation() != null &&
                                 u.getLocation().toLowerCase().contains(loc))
                    .collect(Collectors.toList());
        }
        if (skill != null && !skill.isBlank()) {
            String sk = skill.toLowerCase();
            results = results.stream()
                    .filter(u -> u.getSkillsHave() != null &&
                                 u.getSkillsHave().toLowerCase().contains(sk))
                    .collect(Collectors.toList());
        }

        return results;
    }
}
