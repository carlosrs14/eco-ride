package com.bloque3.notification_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.notification_service.controllers.request.OutboxRequest;
import com.bloque3.notification_service.controllers.response.OutboxResponse;
import com.bloque3.notification_service.models.Outbox;

@Mapper(componentModel = "spring")
public interface OutboxMapper {
    OutboxResponse toDto(Outbox outbox);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "retries", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Outbox toEntity(OutboxRequest outboxRequest);
}
