package com.bloque3.notification_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.notification_service.controllers.request.OutboxRequest;
import com.bloque3.notification_service.controllers.response.OutboxResponse;
import com.bloque3.notification_service.exceptions.ResourceNotFoundException;
import com.bloque3.notification_service.mappers.OutboxMapper;
import com.bloque3.notification_service.models.Outbox;
import com.bloque3.notification_service.repositories.OutboxRepository;
import com.bloque3.notification_service.services.OutboxService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {
    private final OutboxRepository outboxRepository;
    private final OutboxMapper outboxMapper;

    @Override
    public Mono<OutboxResponse> create(OutboxRequest outboxRequest) {
        Outbox outbox = outboxMapper.toEntity(outboxRequest);
        outbox.setCreatedAt(Instant.now());
        outbox.setUpdatedAt(Instant.now());

        return outboxRepository.save(outbox)
            .map(outboxMapper::toDto);
    }

    @Override
    public Mono<OutboxResponse> updateStatus(@NonNull String id, String statusId) {
        UUID uuid = UUID.fromString(id);
        if (uuid == null) throw new NullPointerException("UUID is null");

        return outboxRepository.findById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("outbox", "id", id))
            )
            .flatMap(outbox -> {
                outbox.setStatusId(statusId);
                outbox.setUpdatedAt(Instant.now());
                return outboxRepository.save(outbox);
            })
            .map(outboxMapper::toDto);
    }

    @Override
    public Mono<OutboxResponse> incrementRetries(@NonNull String id) {
        UUID uuid = UUID.fromString(id);
        if (uuid == null) throw new NullPointerException("UUID is null");

        return outboxRepository.findById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("outbox", "id", id))
            )
            .flatMap(outbox -> {
                outbox.setRetries(outbox.getRetries() + 1);
                outbox.setUpdatedAt(Instant.now());
                return outboxRepository.save(outbox);
            })
            .map(outboxMapper::toDto);
    }
}

