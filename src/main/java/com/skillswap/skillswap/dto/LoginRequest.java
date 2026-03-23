package com.skillswap.skillswap.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @Email @NotBlank public String email;
    @NotBlank public String password;
}
