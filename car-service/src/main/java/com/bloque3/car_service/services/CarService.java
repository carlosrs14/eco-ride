package com.bloque3.car_service.services;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarService {
    Mono<CarResponse> create(CarRequest carRequest);
    Mono<CarResponse> findById(String id);
    Mono<CarResponse> update(String id, CarRequest carRequest);
    Flux<CarResponse> findByDriverId(String driverId);
    Mono<Void> delete(String id);
}
