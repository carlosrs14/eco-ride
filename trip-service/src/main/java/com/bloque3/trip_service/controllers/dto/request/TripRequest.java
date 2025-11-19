package com.bloque3.trip_service.controllers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TripRequest {
    @NotNull(message = "Available seats is required")
    private Integer availableSeats;

    @NotNull(message = "Price per seat is required")
    private Double pricePerSeat;

    @NotBlank(message = "Origin ID is required")
    private String originId;

    @NotBlank(message = "Destination ID is required")
    private String destinationId;

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Car ID is required")
    private String carId;
}
