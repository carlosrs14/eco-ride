package com.bloque3.trip_service.models.reservation;

import java.math.BigDecimal;
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
@Table("reservations")
public class Reservation {
    @Id
    private UUID id;

    @Column("trip_id")
    private UUID tripId;

    @Column("passenger_id")
    private UUID passengerId;
    
    @Column("seats_reserved")
    private Integer seatsReserved;

    @Column("amount")
    private BigDecimal amount;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Column("is_active")
    private Boolean isActive;
}
