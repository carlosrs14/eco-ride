package com.bloque3.trip_service.controllers.dto.response;

public record TripResponse(
    String id,
    Integer availableSeats,
    String startTime,
    Double pricePerSeat,
    String originId,
    String destinationId,
    String driverId,
    String carId
) {}
