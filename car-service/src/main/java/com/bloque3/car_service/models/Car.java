package com.bloque3.car_service.models;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("cars")
public class Car {
    @Id
    private String id;
    private String plate;
    private String brand;
    private String model;
    private String color;
    private Integer sits;
    
    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("driver_id")
    private String driverId;
}
