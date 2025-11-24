package com.bloque3.passenger_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.passenger_service.models.Passenger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PassengerRepository extends ReactiveCrudRepository<Passenger, UUID>{

    Mono<Passenger> findByKeycloakSubAndIsActiveTrue(String keycloakSub);
    Mono<Passenger> findByIdAndIsActiveTrue(UUID id);
    Flux<Passenger> findAllByIsActiveTrue();
}
