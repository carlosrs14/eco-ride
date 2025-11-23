package com.bloque3.payment_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.payment_service.models.PaymentIntent;

import reactor.core.publisher.Mono;

@Repository
public interface PaymentIntentRepository extends ReactiveCrudRepository<PaymentIntent,UUID> {

    Mono<PaymentIntent> findByIdAndIsActiveTrue(UUID id);

}
