package com.bloque3.trip_service.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("trips")
public class Trip {
    @Id
    private UUID id;

    @Column("seats_available")
    private Integer seatsAvailable;
    
    @Column("start_time")
    private Instant startTime;
    
    @Column("price_per_seat")
    private BigDecimal pricePerSeat;
    
    @Column("origin_id")
    private UUID originId; 
    
    @Column("destination_id")
    private UUID destinationId;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedBy
    @Column("updated_at")
    private Instant updatedAt;

    @Column("is_active")
    private Boolean isActive;

    @Column("driver_id")
    private UUID driverId;

    @Column("car_id")
    private UUID carId;
}
