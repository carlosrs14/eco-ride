package com.bloque3.trip_service.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.bloque3.trip_service.models.trip.Location;

import reactor.core.publisher.Mono;

public interface LocationRepository extends ReactiveCrudRepository<Location, UUID>{
    
    @Query("SELECT * FROM locations WHERE id = :id AND is_active = TRUE")
    Mono<Location> findActiveById(UUID id);
}
