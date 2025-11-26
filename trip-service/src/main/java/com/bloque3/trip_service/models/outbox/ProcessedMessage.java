package com.bloque3.trip_service.models.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("processed_messages")
public class ProcessedMessage {
    @Id
    private UUID messageId;
    private String eventType;
    private Instant processedAt;
}
