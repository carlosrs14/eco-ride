package com.bloque3.trip_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.trip_service.controllers.dto.request.LocationRequest;
import com.bloque3.trip_service.controllers.dto.response.LocationResponse;
import com.bloque3.trip_service.models.trip.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationResponse toDto(Location location);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Location toEntity(LocationRequest locationRequest); 
    
}
