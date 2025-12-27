package com.delivery.dto;

public record EstablishmentRequestDTO(
    String name,
    String cnpj,
    String address,
    String phone
) {}
