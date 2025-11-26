package com.bloque3.notification_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.notification_service.models.EventType;

import reactor.core.publisher.Mono;

@Repository
public interface EventTypeRepository extends ReactiveCrudRepository<EventType, UUID> {
    Mono<EventType> findByName(String name);
}

