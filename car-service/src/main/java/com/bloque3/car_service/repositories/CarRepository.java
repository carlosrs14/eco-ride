package com.bloque3.car_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.car_service.models.Car;

import reactor.core.publisher.Flux;

@Repository
public interface CarRepository extends ReactiveCrudRepository<Car, String> {
    Flux<Car> findByDriverId(String driverId);
}
