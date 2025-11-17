package com.bloque3.car_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.exception.ResourceNotFoundException;
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
        UUID uuid = UUID.fromString(id);
        return carRepository.findActiveById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("car", "id", id))
            )
            .map(carMapper::toDto);
    }

    @Override
    public Mono<CarResponse> update(@NonNull String id, CarRequest carRequest) {
        UUID uuid = UUID.fromString(id);
        return carRepository.findActiveById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("car", "id", id))
            )
            .flatMap(existingCar -> {
                Car car = carMapper.toEntity(carRequest);
                car.setId(uuid);
                car.setUpdatedAt(Instant.now());
                return carRepository.save(car);
            })
            .map(carMapper::toDto);
    }

    @Override
    public Flux<CarResponse> findByDriverId(String driverId) {
        UUID uuid = UUID.fromString(driverId);
        return carRepository.findByActivesByDriverId(uuid).map(carMapper::toDto);
    }

    @Override
    public Mono<Void> delete(@NonNull String id) {
        UUID uuid = UUID.fromString(id);
        return carRepository.findActiveById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("car", "id", id))
            )
            .flatMap(car -> {
                car.setIsActive(false);
                car.setUpdatedAt(Instant.now());
                return carRepository.save(car);
            })
            .then();
       
    }
    
}
