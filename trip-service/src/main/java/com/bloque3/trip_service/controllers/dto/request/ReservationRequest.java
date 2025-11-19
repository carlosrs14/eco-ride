package com.bloque3.trip_service.controllers.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReservationRequest {
    @NotBlank(message = "Trip id is required")
    private String tripId;

    @NotBlank(message = "Passenger id is required")
    private String passengerId;
    
    @NotBlank(message = "Seats reserved is required")
    private Integer seatsReserved;
}
