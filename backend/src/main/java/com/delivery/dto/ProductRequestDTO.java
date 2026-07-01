package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequestDTO(
    @NotBlank(message = "Product name is required")
    String name,

    @NotBlank(message = "Product description is required")
    String description,

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    Double price,

    Long establishmentId
) {}
