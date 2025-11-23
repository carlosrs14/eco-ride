package com.bloque3.trip_service.services;

import com.bloque3.trip_service.controllers.dto.response.LocationResponse;

import reactor.core.publisher.Mono;

public interface LocationService {
    Mono<LocationResponse> findById(String id);
}
