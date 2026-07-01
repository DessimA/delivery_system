package com.delivery.dto;

public record EstablishmentResponseDTO(
    Long id,
    String name,
    String cnpj,
    String address,
    String phone,
    boolean active
) {}
