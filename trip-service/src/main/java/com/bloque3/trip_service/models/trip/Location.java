package com.bloque3.trip_service.models.trip;

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
@Table("locations")
public class Location {
    @Id
    private UUID id;

    private String name; 
    
    @Column("location_type_id")
    private UUID locationTypeId;
    
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedBy
    @Column("updated_at")
    private Instant updatedAt;

    @Column("is_active")
    private Boolean isActive;
}
