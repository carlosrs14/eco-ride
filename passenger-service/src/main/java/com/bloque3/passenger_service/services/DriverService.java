package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.response.DriverResponseDTO;

import reactor.core.publisher.Mono;

public interface DriverService {
    Mono<DriverResponseDTO> create(DriverRequestDTO driverRequestDTO);
    Mono<DriverResponseDTO> findById(String id);
    Mono<DriverResponseDTO> update(String id, DriverRequestUpdateDTO driverRequestUpdateDTO);
    Mono<Void> delete(String id);

}
