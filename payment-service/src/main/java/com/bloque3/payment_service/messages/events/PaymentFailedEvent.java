package com.bloque3.payment_service.messages.events;

import java.util.UUID;

import lombok.Builder;

@Builder
public record PaymentFailedEvent(
    UUID reservationId,
    String reason,
    UUID sagaId,
    UUID messageId,
    String eventType
) {}
