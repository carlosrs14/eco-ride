package com.bloque3.trip_service.models.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String eventType;
    private String payload; // JSON string
    private UUID messageId;
    private UUID sagaId;
    private String status;
    private Integer retryCount;
    private Instant createdAt;
    private Instant processedAt;
    private String lastError;
}
