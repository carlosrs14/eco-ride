package com.bloque3.trip_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.trip_service.controllers.dto.request.TripRequest;
import com.bloque3.trip_service.controllers.dto.response.TripResponse;
import com.bloque3.trip_service.models.trip.Trip;

@Mapper(componentModel = "spring")
public interface TripMapper {

    TripResponse toDto(Trip trip);
        
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Trip toEntity(TripRequest tripRequest); 
}
