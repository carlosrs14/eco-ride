package com.bloque3.notification_service.controllers.request;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OutboxRequest(
    @NotBlank(message = "Event type id is required")
    String eventTypeId,
    
    @NotNull(message = "Payload is required")
    @Size(min = 1, message = "Payload cannot be empty")
    Map<String, Object> payload,
    
    @NotBlank(message = "Status id is required")
    String statusId

) {}
