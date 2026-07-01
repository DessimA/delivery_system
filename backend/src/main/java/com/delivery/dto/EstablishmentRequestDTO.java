package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;

public record EstablishmentRequestDTO(
    @NotBlank(message = "Name is required")
    String name,

    String cnpj,

    @NotBlank(message = "Address is required")
    String address,

    String phone
) {}
