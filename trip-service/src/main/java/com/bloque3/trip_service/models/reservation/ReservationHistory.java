package com.bloque3.trip_service.models.reservation;

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
@Table("reservation_history")
public class ReservationHistory {
    @Id
    private UUID id;

    @Column("reservation_id")
    private UUID reservationId;

    @Column("position")
    private Integer order;

    @Column("reservation_status_id")
    private UUID reservationStatusId;
    
    @Column("is_active")
    private Boolean isActive;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
    
}
