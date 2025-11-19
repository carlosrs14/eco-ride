package com.bloque3.passenger_service.mappers;
    
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.models.Driver;


@Mapper(componentModel = "spring")
public interface DriverMapper {

    DriverResponseDTO toDto(Driver driver);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "verificationStatus", ignore = true)
    Driver toEntity(DriverRequestDTO driverRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passengerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Driver toEntity(DriverRequestUpdateDTO driverRequestUpdateDTO);
}


