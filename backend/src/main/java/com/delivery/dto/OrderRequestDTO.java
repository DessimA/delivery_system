package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO(
    @NotBlank(message = "Delivery address is required")
    String deliveryAddress,

    @NotEmpty(message = "At least one item is required")
    List<OrderItemRequestDTO> items
) {}
