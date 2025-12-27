package com.delivery.dto;

public record ProductRequestDTO(
    String name,
    String description,
    Double price,
    Long establishmentId
) {}
