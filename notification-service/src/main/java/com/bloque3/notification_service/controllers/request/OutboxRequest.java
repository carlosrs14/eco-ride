package com.bloque3.notification_service.controllers.request;

import jakarta.validation.constraints.NotBlank;

public record OutboxRequest(
    @NotBlank(message = "Event type id is required")
    String eventTypeId,
    
    @NotBlank(message = "Payload is required")
    String payload,
    
    @NotBlank(message = "Status id is required")
    String statusId

) {}
