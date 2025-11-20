package com.bloque3.trip_service.services;

import com.bloque3.trip_service.controllers.dto.request.ReservationRequest;
import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationService {
    Mono<ReservationResponse> create(ReservationRequest reservationRequest);
    Mono<ReservationResponse> findById(String id);
    Flux<ReservationResponse> findByTripId(String tripId);
    Flux<ReservationResponse> findByPassengerId(String passengerId);
    Mono<ReservationResponse> update(String id, ReservationRequest reservationRequest);
    Mono<Void> delete(String id);
}
