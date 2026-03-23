package com.skillswap.skillswap.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    public Long id;
    public String name;
    public String email;
    public String skillsHave;
    public String skillsWant;
    public String languages;
    public String bio;
    public String location;
    public String avatarBase64;
    public String role;
    public String token;
}
