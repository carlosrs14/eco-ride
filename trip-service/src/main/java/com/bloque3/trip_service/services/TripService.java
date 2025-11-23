package com.bloque3.trip_service.services;

import com.bloque3.trip_service.controllers.dto.request.TripRequest;
import com.bloque3.trip_service.controllers.dto.response.TripResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TripService {
    Mono<TripResponse> create(TripRequest tripRequest);
    Mono<TripResponse> findById(String id);
    Flux<TripResponse> findByDriverId(String driverId);
    Flux<TripResponse> searchTrips(String originId, String destinationId);
    Mono<TripResponse> update(String id, TripRequest tripRequest);
    Mono<Void> delete(String id);
}
