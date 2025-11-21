package com.bloque3.trip_service.controllers.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TripRequest(
    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "Available seats must be greater than 0")
    Integer availableSeats,
    
    @Future(message = "Start time must be in the future")
    @NotNull(message = "Start time is required")
    Instant startTime,

    @NotNull(message = "Price per seat is required")
    Double pricePerSeat,

    @NotBlank(message = "Origin ID is required")
    String originId,

    @NotBlank(message = "Destination ID is required")
    String destinationId,

    @NotBlank(message = "Driver ID is required")
    String driverId,

    @NotBlank(message = "Car ID is required")
    String carId
){}
