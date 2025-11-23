package com.bloque3.trip_service.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.bloque3.trip_service.models.outbox.OutboxEvent;

import reactor.core.publisher.Flux;

public interface OutboxRepository extends ReactiveCrudRepository<OutboxEvent, UUID> {
    @Query(""" 
        SELECT * 
        FROM outbox_events 
        WHERE status = 'PENDING' 
        ORDER BY created_at 
        LIMIT :limit
    """)
    Flux<OutboxEvent> findPending(int limit);
}
