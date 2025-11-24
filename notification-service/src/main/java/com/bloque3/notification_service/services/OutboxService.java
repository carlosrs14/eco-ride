package com.bloque3.notification_service.services;

import com.bloque3.notification_service.controllers.request.OutboxRequest;
import com.bloque3.notification_service.controllers.response.OutboxResponse;


import reactor.core.publisher.Mono;

public interface OutboxService {
    Mono<OutboxResponse> create(OutboxRequest outboxRequest);
    Mono<OutboxResponse> updateStatus(String id, String statusId);
    Mono<OutboxResponse> incrementRetries(String id);
}
