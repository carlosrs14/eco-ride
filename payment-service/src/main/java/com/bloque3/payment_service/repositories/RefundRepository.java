package com.bloque3.payment_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.payment_service.models.Refund;

@Repository
public interface RefundRepository extends ReactiveCrudRepository<Refund, UUID> {

}
