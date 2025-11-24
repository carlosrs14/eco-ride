package com.bloque3.notification_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.notification_service.models.Outbox;

@Repository
public interface OutboxRepository extends ReactiveCrudRepository<Outbox, UUID> {
    
}
