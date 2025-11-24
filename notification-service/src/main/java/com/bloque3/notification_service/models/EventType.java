package com.bloque3.notification_service.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Table("event_types")
public class EventType {
    @Id
    private UUID id;
    
    private String name;
}
