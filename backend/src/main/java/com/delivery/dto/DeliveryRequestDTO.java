package com.delivery.dto;

public record DeliveryRequestDTO(
    Long orderId,
    String originAddress,
    String destinationAddress
) {}
