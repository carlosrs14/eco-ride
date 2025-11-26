package com.bloque3.trip_service.controllers.dto.response;

import java.math.BigDecimal;

public record TripResponse(
    String id,
    Integer availableSeats,
    String startTime,
    BigDecimal pricePerSeat,
    String originId,
    String destinationId,
    String driverId,
    String carId
) {}
