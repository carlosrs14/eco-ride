package com.bloque3.passenger_service.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("ratings")
public class Rating {

    @Id
    private UUID id;

    @Column("trip_id")
    private UUID tripId;
    
    @Column("from_id")
    private UUID fromId;

    @Column("to_id")
    private UUID toId;

    private Float score;

    private String comment;
}
