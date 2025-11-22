package com.bloque3.passenger_service.clients.dto;

public record TripResponseDTO(
    String id,
    Integer availableSeats,
    String startTime,
    Double pricePerSeat,
    String originId,
    String destinationId,
    String driverId,
    String carId
) {}