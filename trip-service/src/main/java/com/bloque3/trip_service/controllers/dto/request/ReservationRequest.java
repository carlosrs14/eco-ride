package com.bloque3.trip_service.controllers.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReservationRequest(
    @NotBlank(message = "Trip id is required")
    String tripId,

    @NotBlank(message = "Passenger id is required")
    String passengerId,
    
    @NotBlank(message = "Seats reserved is required")
    Integer seatsReserved
){}

