package com.delivery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record UserUpdateRequestDTO(
    @NotBlank(message = "Name cannot be empty")
    String name,

    @NotBlank(message = "CPF cannot be empty")
    String cpf,

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,

    @NotBlank(message = "Address cannot be empty")
    String address,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String email,

    String password
) {}
