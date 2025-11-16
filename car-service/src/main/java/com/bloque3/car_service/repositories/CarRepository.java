package com.bloque3.car_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.car_service.models.Car;

@Repository
public interface CarRepository extends ReactiveCrudRepository<Car, String> {
    
}
