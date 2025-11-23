package com.bloque3.trip_service.repositories;
import org.springframework.stereotype.Repository;

import com.bloque3.trip_service.models.trip.Trip;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@Repository
public interface TripRepository extends ReactiveCrudRepository<Trip, UUID> {

    @Query("SELECT * FROM trips WHERE id = :id AND is_active = TRUE")
    Mono<Trip> findActiveById(UUID id);

    @Query("SELECT * FROM trips WHERE driver_id = :driverId AND is_active = TRUE")
    Flux<Trip> findActiveByDriverId(UUID driverId);

    @Query("""
        SELECT * FROM trips 
        WHERE (is_active = TRUE)
        AND (origin_id = :originId)
        AND (destination_id = :destinationId)
    """)
    Flux<Trip> searchTrips(UUID originId, UUID destinationId);
}
