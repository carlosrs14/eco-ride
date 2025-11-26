package com.bloque3.passenger_service.controllers.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PassengerRequestDTO(
    @NotBlank(message = "Name is required")
    String name,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "KeycloakSub is required")
    String keycloakSub

) {}
