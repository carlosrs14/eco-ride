package com.bloque3.trip_service.controllers.dto.response;

import java.math.BigDecimal;

public record ReservationResponse(
    String id,
    String tripId,
    BigDecimal amount,
    String passengerId,
    Integer seatsReserved
) {}
