package com.bloque3.car_service.services.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.mappers.CarMapper;
import com.bloque3.car_service.models.Car;
import com.bloque3.car_service.repositories.CarRepository;
import com.bloque3.car_service.services.CarService;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public CarServiceImpl(CarRepository carRepository, CarMapper carMapper) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
    }

    @Override
    public Mono<CarResponse> create(CarRequest carRequest) {
        Car car = carMapper.toEntity(carRequest);
        car.setIsActive(true);
        car.setCreatedAt(Instant.now());
        car.setUpdatedAt(Instant.now());
        return carRepository.save(car).map(carMapper::toDto);
    }

    @Override
    public Mono<CarResponse> findById(@NonNull String id) {
        return carRepository.findById(id).map(carMapper::toDto);
    }

    @Override
    public Mono<CarResponse> update(String id, CarRequest carRequest) {
        Car car = carMapper.toEntity(carRequest);
        car.setId(id);
        car.setUpdatedAt(Instant.now());
        return carRepository.save(car).map(carMapper::toDto);
    }

    @Override
    public Flux<CarResponse> findByDriverId(String driverId) {
        return carRepository.findByDriverId(driverId).map(carMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        Car car = Car.builder()
            .id(id)
            .updatedAt(Instant.now())
            .isActive(false)
            .build();

        if (car == null) throw new RuntimeException("Car not found");
        return carRepository.save(car).then();
    }
    
}
