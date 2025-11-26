package com.bloque3.notification_service.models;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("templates")
public class Template implements Persistable<UUID> {
    @Id
    private UUID id;

    private String code;
    
    @Column("channel_id")
    private UUID channelId;
    
    private String subject;
    
    private String body;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("is_active")
    private Boolean isActive;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return this.isNew || id == null;
    }
}
