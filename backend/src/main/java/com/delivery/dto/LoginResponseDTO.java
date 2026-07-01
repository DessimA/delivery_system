package com.delivery.dto;

public record LoginResponseDTO(
    String accessToken,
    String tokenType
) {
    public LoginResponseDTO(String accessToken) {
        this(accessToken, "Bearer");
    }
}