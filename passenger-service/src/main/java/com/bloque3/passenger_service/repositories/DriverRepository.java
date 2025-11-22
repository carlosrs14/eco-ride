package com.bloque3.passenger_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.passenger_service.models.Driver;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DriverRepository extends ReactiveCrudRepository<Driver, UUID> {
    
    Mono<Driver> findByIdAndIsActiveTrue(UUID id);
    Flux<Driver> findAllByIsActiveTrue();

}
