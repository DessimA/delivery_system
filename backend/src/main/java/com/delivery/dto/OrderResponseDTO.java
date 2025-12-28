package com.delivery.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    Long id,
    Long customerId,
    String deliveryAddress,
    Float deliveryFee,
    String status,
    LocalDateTime orderDate,
    List<ProductResponseDTO> products,
    Float total,
    DeliveryResponseDTO delivery
) {}
