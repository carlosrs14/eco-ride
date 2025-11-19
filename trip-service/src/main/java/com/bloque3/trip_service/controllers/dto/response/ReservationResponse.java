package com.bloque3.trip_service.controllers.dto.response;

public record ReservationResponse(
    String id,
    String tripId,
    String passengerId,
    Integer seatsReserved
) {}
