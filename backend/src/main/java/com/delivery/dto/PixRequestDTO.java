package com.delivery.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PixRequestDTO(
    @NotNull Long orderId,
    @NotNull BigDecimal amount
) {}
