package com.bloque3.trip_service.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.bloque3.trip_service.models.outbox.ProcessedMessage;

import reactor.core.publisher.Mono;

public interface ProcessedMessageRepository extends ReactiveCrudRepository<ProcessedMessage, UUID> {
    @Query("""
        SELECT EXISTS(
            SELECT 1 
            FROM processed_messages
            WHERE message_Id = :messageId 
        ) 
    """)
    Mono<Boolean> existsByMessageId(UUID messageId);
}
