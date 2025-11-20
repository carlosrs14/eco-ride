package com.bloque3.trip_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.trip_service.controllers.dto.request.ReservationRequest;
import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;
import com.bloque3.trip_service.models.reservation.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationResponse toDto(Reservation reservation);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Reservation toEntity(ReservationRequest reservationRequest); 
}
