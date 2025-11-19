package com.bloque3.trip_service.models.trip;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("trip_history")
public class TripHistory {
    @Id
    private UUID id;

    @Column("trip_id")
    private UUID tripId;

    private Integer order;
    
    @Column("trip_state_id")
    private UUID tripStateId;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("is_active")
    private Boolean isActive;
    
}
