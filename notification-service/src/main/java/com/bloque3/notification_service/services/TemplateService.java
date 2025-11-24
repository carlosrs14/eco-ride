package com.bloque3.notification_service.services;

import com.bloque3.notification_service.controllers.request.TemplateRequest;
import com.bloque3.notification_service.controllers.response.TemplateResponse;

import reactor.core.publisher.Mono;

public interface TemplateService {
    Mono<TemplateResponse> create(TemplateRequest templateRequest);
    Mono<TemplateResponse> findById(String id);
    Mono<TemplateResponse> findByCode(String code);
    Mono<TemplateResponse> update(String id, TemplateRequest templateRequest);
    Mono<Void> delete(String id);
}
