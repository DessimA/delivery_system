package com.delivery.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
    Long productId,
    String productName,
    String productImageUrl,
    int quantity,
    BigDecimal unitPrice
) {}
