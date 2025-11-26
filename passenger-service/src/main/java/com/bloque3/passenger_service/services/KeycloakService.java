package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;

import reactor.core.publisher.Mono;

public interface KeycloakService {
    Mono<String> createuser(PassengerRequestDTO requestDTO);
}
