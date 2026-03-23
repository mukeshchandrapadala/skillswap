package com.skillswap.skillswap.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(true, message, data);
    }
    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message, null);
    }
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
}
