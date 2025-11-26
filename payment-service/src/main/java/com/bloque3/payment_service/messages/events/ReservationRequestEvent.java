package com.bloque3.payment_service.messages.events;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ReservationRequestEvent(
    UUID id,
    UUID tripId,
    UUID passengerId,
    BigDecimal amount,
    UUID sagaId,
    UUID messageId,
    String eventType
) {}
