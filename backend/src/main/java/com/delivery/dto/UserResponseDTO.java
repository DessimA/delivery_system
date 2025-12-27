package com.delivery.dto;

import java.time.LocalDate;
import java.util.List;

public record UserResponseDTO(
    Long id,
    String name,
    String cpf,
    LocalDate birthDate,
    String address,
    String email,
    List<String> roles
) {}
