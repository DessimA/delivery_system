package com.delivery.dto;

public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    Double price,
    String imageUrl
) {}
