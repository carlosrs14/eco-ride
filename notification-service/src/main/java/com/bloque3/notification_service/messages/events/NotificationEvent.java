package com.bloque3.notification_service.messages.events;

import java.util.Map;

public record NotificationEvent(
    String templateCode,
    String to,
    Map<String, Object> params

) {}
