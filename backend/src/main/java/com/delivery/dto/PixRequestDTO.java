package com.delivery.dto;

import jakarta.validation.constraints.NotNull;

public record PixRequestDTO(
    @NotNull Long orderId,
    @NotNull Double amount
) {}
