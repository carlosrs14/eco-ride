package com.bloque3.payment_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.payment_service.models.Charge;

import reactor.core.publisher.Mono;

@Repository
public interface ChargeRepository extends ReactiveCrudRepository<Charge, UUID> {

    Mono<Charge> findByIdAndIsActiveTrue(UUID id);

}
