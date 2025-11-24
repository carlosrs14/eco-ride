package com.bloque3.notification_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.notification_service.controllers.request.TemplateRequest;
import com.bloque3.notification_service.controllers.response.TemplateResponse;
import com.bloque3.notification_service.models.Template;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    TemplateResponse toDto(Template template);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Template toEntity(TemplateRequest templateRequest);
}
