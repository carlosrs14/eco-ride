package com.bloque3.notification_service.controllers.response;

public record OutboxResponse(
    String id,
    String eventTypeId,
    String payload,
    String statusId,
    Integer retries
) {}
