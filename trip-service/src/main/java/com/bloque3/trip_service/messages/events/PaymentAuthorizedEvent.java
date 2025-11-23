package com.bloque3.trip_service.messages.events;

import java.util.UUID;

import lombok.Builder;

@Builder
public record PaymentAuthorizedEvent(
    UUID reservationId,
    String paymentIntentId,
    String chargeId,
    UUID sagaId,
    UUID messageId,
    String eventType // "PaymentAuthorized.v1"
) {}
