package com.bloque3.trip_service.messages.events;

import java.util.UUID;

import lombok.Builder;

@Builder
public record PaymentFailed(
    UUID reservationId,
    String reason,
    UUID sagaId,
    UUID messageId,
    String eventType // PaymentFailed.v1
) {}
