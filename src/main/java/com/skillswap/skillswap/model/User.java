package com.skillswap.skillswap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String password;

    private String skillsHave;
    private String skillsWant;
    private String languages;

    @Column(length = 500)
    private String bio;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String avatarBase64;

    @Builder.Default
    private String role = "USER";

    // Online/offline status
    @Builder.Default
    @Column(name = "is_online")
    private boolean online = false;

    private LocalDateTime lastSeen;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
