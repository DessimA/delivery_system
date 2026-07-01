package com.delivery.dto;

public record PixResponseDTO(
    String qrCode,
    String copyPaste,
    String transactionId,
    String expiresAt,
    Double amount
) {}
