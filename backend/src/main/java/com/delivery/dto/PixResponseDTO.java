package com.delivery.dto;

import java.math.BigDecimal;

public record PixResponseDTO(
    String qrCode,
    String copyPaste,
    String transactionId,
    String expiresAt,
    BigDecimal amount
) {}
