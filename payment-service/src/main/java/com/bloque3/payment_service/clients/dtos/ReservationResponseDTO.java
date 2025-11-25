package com.bloque3.payment_service.clients.dtos;

public record ReservationResponseDTO(
    String id,
    String tripId,
    String passengerId,
    Integer seatsReserved
) {}
