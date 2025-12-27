package com.delivery.dto;

import java.util.List;

public record OrderRequestDTO(
    String deliveryAddress,
    List<Long> productIds
) {}
