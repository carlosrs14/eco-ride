package com.bloque3.notification_service.controllers.response;

public record TemplateResponse(
    String id,
    String code,
    String channelId,
    String subject,
    String body
) {}
