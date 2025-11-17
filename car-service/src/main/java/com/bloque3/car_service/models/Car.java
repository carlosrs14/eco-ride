package com.bloque3.car_service.models;

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
@Table("cars")
public class Car {
    @Id
    private UUID id;
    private String plate;
    private String brand;
    private String model;
    private String color;
    private Integer seats;
    
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("driver_id")
    private UUID driverId;

    @Column("is_active")
    private Boolean isActive;
}
