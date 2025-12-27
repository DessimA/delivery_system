package com.delivery.dto;

import java.time.LocalDate;

public record UserRequestDTO(
    String name,
    String cpf,
    LocalDate birthDate,
    String address,
    String email,
    String password
) {}
