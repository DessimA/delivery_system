package com.delivery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserRequestDTO(
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

    @Size(min = 8, message = "Password must have at least 8 characters")
    String password
) {}
