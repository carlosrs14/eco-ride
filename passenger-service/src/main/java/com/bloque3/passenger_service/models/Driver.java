package com.bloque3.passenger_service.models;

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
@Table("drivers")
public class Driver {

    @Id
    private UUID id;

    @Column("passenger_id")
    private UUID passengerId;

    @Column("license_no")
    private String licenseNo;

    @Column("verification_status")
    private Boolean verificationStatus;
    
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("is_active")
    private Boolean isActive;
    
}
