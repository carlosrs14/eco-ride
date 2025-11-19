package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.passengerDtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.passengerDtos.response.PassengerResponseDTO;

import reactor.core.publisher.Mono;

public interface PassengerService {

    Mono<PassengerResponseDTO> create(PassengerRequestDTO passengerRequestDTO);
    Mono<PassengerResponseDTO> findById(String id);
    Mono<PassengerResponseDTO> update(String id, PassengerRequestDTO passengerRequestDTO);
    Mono<Void> delete(String id);
    Mono<PassengerResponseDTO> findByKeycloakSub(String keycloakSub);
}
