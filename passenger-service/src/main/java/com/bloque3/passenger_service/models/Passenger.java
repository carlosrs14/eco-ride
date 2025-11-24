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
@Table("passengers")
public class Passenger {

    @Id
    private UUID id;
    @Column("keycloak_sub")
    private String keycloakSub;
    private String name;
    private String email;

    @Column("rating_avg")
    private Float ratingAvg;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Column("is_active")
    private Boolean isActive;

}
