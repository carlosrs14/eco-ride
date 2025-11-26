package com.bloque3.notification_service.controllers.request;

import jakarta.validation.constraints.NotBlank;

public record TemplateRequest(
    @NotBlank(message = "Code is requiered")
    String code,
    
    @NotBlank(message= "Channel id is required")
    String channelId,
    
    @NotBlank(message = "Subject is required")
    String subject,
    
    @NotBlank(message = "Body is required")
    String body
) {}

