package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeliveryRequestDTO(
    @NotNull(message = "Order ID is required")
    Long orderId,

    @NotBlank(message = "Origin address is required")
    String originAddress,

    @NotBlank(message = "Destination address is required")
    String destinationAddress
) {}
