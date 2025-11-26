package com.bloque3.passenger_service.controllers.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PassengerRequestDTO(
    @NotBlank(message = "username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "Name is required")
    String name
) {}
