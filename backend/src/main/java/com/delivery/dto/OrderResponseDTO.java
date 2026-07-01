package com.delivery.dto;

import com.delivery.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    Long id,
    Long customerId,
    String deliveryAddress,
    BigDecimal deliveryFee,
    OrderStatus status,
    LocalDateTime orderDate,
    List<ProductResponseDTO> products,
    BigDecimal total,
    DeliveryResponseDTO delivery
) {}
