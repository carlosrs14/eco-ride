package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestUpdateDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DriverService {
    Mono<DriverResponseDTO> create(DriverRequestDTO driverRequestDTO);
    Mono<DriverResponseDTO> findById(String id);
    Flux<DriverResponseDTO> findAll();
    Mono<DriverResponseDTO> update(String id, DriverRequestUpdateDTO driverRequestUpdateDTO);
    Mono<Void> delete(String id);

}
