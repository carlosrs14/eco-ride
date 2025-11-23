package com.bloque3.trip_service.repositories;


import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.trip_service.models.reservation.Reservation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReservationRepository extends ReactiveCrudRepository<Reservation, UUID> {

    @Query("SELECT * FROM reservations WHERE id = :id AND is_active = TRUE")
    Mono<Reservation> findActiveByid(UUID id);

    @Query("SELECT * FROM reservations WHERE trip_id = :tripId AND is_active = TRUE")
    Flux<Reservation> findByTripId(UUID tripId);

    @Query("SELECT * FROM reservations WHERE passenger_id = :passengerId AND is_active = TRUE")
    Flux<Reservation> findByPassengerId(UUID passengerId);
}
