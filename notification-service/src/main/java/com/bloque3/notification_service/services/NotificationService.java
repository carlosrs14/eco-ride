package com.bloque3.notification_service.services;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<Void> notify(String type, String templateCode, String to, Map<String, Object> params);
}
