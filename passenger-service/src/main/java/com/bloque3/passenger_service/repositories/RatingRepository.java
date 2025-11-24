package com.bloque3.passenger_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.passenger_service.models.Rating;

import reactor.core.publisher.Flux;

@Repository
public interface RatingRepository extends ReactiveCrudRepository<Rating, UUID>{
    
    Flux<Rating> findAllByFromId(UUID passengerId);

}
