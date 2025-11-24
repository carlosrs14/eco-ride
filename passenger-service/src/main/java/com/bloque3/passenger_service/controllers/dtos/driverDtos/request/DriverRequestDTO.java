package com.bloque3.passenger_service.controllers.dtos.driverDtos.request;

import jakarta.validation.constraints.NotBlank;

public record DriverRequestDTO(
    @NotBlank(message = "Passenger ID is required") 
    String passengerId,

    @NotBlank(message = "License number is required") 
    String licenseNo
) {}
