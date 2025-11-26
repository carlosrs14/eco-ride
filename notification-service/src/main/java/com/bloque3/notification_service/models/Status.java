package com.bloque3.notification_service.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data   
@Builder
@Table("status")
public class Status {
    @Id
    private UUID id;
    
    private String name;
    
}
