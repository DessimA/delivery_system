package com.delivery.dto;

import com.delivery.model.DeliveryStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryResponseDTO(
    Long id,
    Long orderId,
    Long courierId,
    String originAddress,
    String destinationAddress,
    DeliveryStatus status,
    Integer estimatedTimeMinutes,
    BigDecimal fee,
    LocalDateTime createdAt,
    LocalDateTime deliveredAt
) {}