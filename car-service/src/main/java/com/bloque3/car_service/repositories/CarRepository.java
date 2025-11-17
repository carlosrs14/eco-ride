package com.bloque3.car_service.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.car_service.models.Car;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CarRepository extends ReactiveCrudRepository<Car, UUID> {
    
    Flux<Car> findByDriverId(UUID driverId);

    @Query("SELECT * FROM cars WHERE driver_id = :driverId AND is_active = TRUE")
    Flux<Car> findByActivesByDriverId(UUID driverId);

    @Query("SELECT * FROM cars WHERE id = :id AND is_active = TRUE")
    Mono<Car> findActiveById(UUID id);
}
