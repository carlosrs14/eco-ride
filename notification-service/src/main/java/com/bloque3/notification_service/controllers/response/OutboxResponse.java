package com.bloque3.notification_service.controllers.response;

import java.util.Map;

public record OutboxResponse(
    String id,
    String eventTypeId,
    Map<String, Object> payload,
    String statusId,
    Integer retries
) {}
