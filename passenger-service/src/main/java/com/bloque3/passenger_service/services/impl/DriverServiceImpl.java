package com.bloque3.passenger_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.passenger_service.controllers.dtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.exceptions.ResourceNotFoundException;
import com.bloque3.passenger_service.mappers.DriverMapper;
import com.bloque3.passenger_service.models.Driver;
import com.bloque3.passenger_service.repositories.DriverRepository;
import com.bloque3.passenger_service.services.DriverService;
import com.bloque3.passenger_service.services.PassengerService;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DriverServiceImpl implements DriverService{

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final PassengerService passengerService;


    public DriverServiceImpl(DriverRepository driverRepository, DriverMapper driverMapper, PassengerService passengerService) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
        this.passengerService = passengerService;
    }
        

    @Override
    public Mono<DriverResponseDTO> create(DriverRequestDTO driverRequestDTO) {
        return passengerService.findById(driverRequestDTO.passengerId())
                .flatMap(driv -> {
                    Driver driver = driverMapper.toEntity(driverRequestDTO);
                    driver.setIsActive(true);
                    driver.setCreatedAt(Instant.now());
                    driver.setUpdatedAt(Instant.now());
                    return driverRepository.save(driver);
                }).map(driverMapper::toDto);
    }

    @Override
    public Mono<DriverResponseDTO> findById(@NonNull String id) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> driverRepository.findByIdAndIsActiveTrue(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("driver", "id", id))
                )
                .map(
                    driverMapper::toDto 
                ));
    }

    @Override
    public Mono<DriverResponseDTO> update(@NonNull String id, DriverRequestUpdateDTO driverRequestUpdateDTO) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> driverRepository.findByIdAndIsActiveTrue(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("passenger", "id", id))
                )
                .flatMap(driv -> {
                    Driver driver = driverMapper.toEntity(driverRequestUpdateDTO);
                    driver.setId(uuid);
                    driver.setUpdatedAt(Instant.now());
                    return driverRepository.save(driver);
                })
                .map(driverMapper::toDto));
    }   

    @Override
    public Mono<Void> delete(@NonNull String id) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> driverRepository.findByIdAndIsActiveTrue(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("id", id, uuid))
                )
                .flatMap(driv -> {
                    driv.setIsActive(false);
                    driv.setUpdatedAt(Instant.now());
                    return driverRepository.save(driv);
                })
                .then());
    }


    @Override
    public Flux<DriverResponseDTO> findAll() {
        return driverRepository.findAllByIsActiveTrue()
        .map(
            driverMapper::toDto
        );
    }
}
