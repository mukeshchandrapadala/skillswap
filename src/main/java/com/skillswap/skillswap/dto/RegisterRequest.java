package com.skillswap.skillswap.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @NotBlank public String name;
    @Email @NotBlank public String email;
    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") public String password;
    public String skillsHave;
    public String skillsWant;
    public String languages;
    public String bio;
    public String location;
}
