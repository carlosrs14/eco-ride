package com.bloque3.notification_service.models;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("outboxes")
public class Outbox {
    @Id
    private UUID id;
    
    @Column("event_type_id")
    private String eventTypeId;

    private Map<String, Object> payload;
    
    @Column("status_id")
    private String statusId;
    
    private Integer retries;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
}
