package com.bloque3.notification_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.notification_service.controllers.request.TemplateRequest;
import com.bloque3.notification_service.controllers.response.TemplateResponse;
import com.bloque3.notification_service.exceptions.ResourceNotFoundException;
import com.bloque3.notification_service.mappers.TemplateMapper;
import com.bloque3.notification_service.models.Template;
import com.bloque3.notification_service.repositories.TemplateRepository;
import com.bloque3.notification_service.services.TemplateService;

import lombok.NonNull;
import reactor.core.publisher.Mono;

@Service
public class TemplateServiceImpl implements TemplateService{

    private final TemplateMapper templateMapper;
    private final TemplateRepository templateRepository;


    public TemplateServiceImpl(TemplateMapper templateMapper, TemplateRepository templateRepository) {
        this.templateMapper = templateMapper;
        this.templateRepository = templateRepository;
    }


    @Override
    public Mono<TemplateResponse> create(TemplateRequest templateRequest) {
        Template template = templateMapper.toEntity(templateRequest);
        template.setIsActive(true);
        template.setCreatedAt(Instant.now());
        template.setUpdatedAt(Instant.now());
        return templateRepository.save(template).map(templateMapper::toDto);
    }


    @Override
    public Mono<TemplateResponse> findById(@NonNull String id) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> templateRepository.findActiveById(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("template", "id", id))
                )
                .map(templateMapper::toDto));
    }
    
    @Override
    public Mono<TemplateResponse> findByCode(String code) {
        return templateRepository.findByCode(code)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("template", "code", code))
            )
            .map(templateMapper::toDto);
    }

    @Override
    public Mono<TemplateResponse> update(@NonNull String id, TemplateRequest templateRequest) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> templateRepository.findActiveById(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("template", "id", id))
                )
                .flatMap(existingTemplate -> {
                    Template template = templateMapper.toEntity(templateRequest);
                    template.setId(uuid);
                    template.setUpdatedAt(Instant.now());
                    return templateRepository.save(template);
                })
                .map(templateMapper::toDto));
    }

    @Override
    public Mono<Void> delete(@NonNull String id) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> templateRepository.findActiveById(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("template", "id", id))
                )
                .flatMap(template -> {
                    template.setIsActive(false);
                    template.setUpdatedAt(Instant.now());
                    return templateRepository.save(template);
                })
                .then());
    }
    
}
