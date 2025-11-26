package com.bloque3.passenger_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.models.Passenger;

@Mapper(componentModel = "spring")
public interface PassengerMapper {  
    
    PassengerResponseDTO toDto(Passenger passenger);    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "ratingAvg", ignore = true)
    @Mapping(target = "keycloakSub", ignore = true)
    Passenger toEntity(PassengerRequestDTO passengerRequestDTO);
}
