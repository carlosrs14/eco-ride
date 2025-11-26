package com.bloque3.passenger_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.exceptions.ResourceNotFoundException;
import com.bloque3.passenger_service.mappers.PassengerMapper;
import com.bloque3.passenger_service.models.Passenger;
import com.bloque3.passenger_service.repositories.PassengerRepository;
import com.bloque3.passenger_service.services.KeycloakService;
import com.bloque3.passenger_service.services.PassengerService;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final KeycloakService keycloakService;

    @Override
    public Mono<PassengerResponseDTO> create(@Valid PassengerRequestDTO passengerRequestDTO) {
        Passenger passenger = passengerMapper.toEntity(passengerRequestDTO);
        passenger.setIsActive(true);
        passenger.setCreatedAt(Instant.now());
        passenger.setUpdatedAt(Instant.now());

        return keycloakService.createuser(passengerRequestDTO)
        .flatMap(keycloakSub -> {
            passenger.setKeycloakSub(keycloakSub);
            return passengerRepository.save(passenger);
        })
        .map(passengerMapper::toDto);

    }

    @Override
    public Mono<PassengerResponseDTO> findById(@NonNull String id) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> passengerRepository.findByIdAndIsActiveTrue(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("passenger", "id", id))
                )
                .map(
                    passengerMapper::toDto
                ));
    }

    @Override
    public Mono<PassengerResponseDTO> update(@NonNull String id, PassengerRequestDTO passengerRequestDTO) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> passengerRepository.findByIdAndIsActiveTrue(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("passenger", "id", id))
                )
                .flatMap(passen ->{
                    Passenger passenger = passengerMapper.toEntity(passengerRequestDTO);
                    passenger.setId(uuid);
                    passen.setUpdatedAt(Instant.now());
                    return passengerRepository.save(passen);

                }).map(passengerMapper:: toDto));
    }

    @Override
    public Mono<Void> delete(@NonNull String id) {
        return Mono.fromCallable(() -> UUID.fromString(id))
            .onErrorResume(IllegalArgumentException.class, Mono::error)
            .flatMap(uuid -> passengerRepository.findByIdAndIsActiveTrue(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("passenger", "id", id))
                )
                .flatMap(passen ->{
                    passen.setIsActive(false);
                    passen.setUpdatedAt(Instant.now());
                    return passengerRepository.save(passen);
                }).then());
    }

    @Override
    public Mono<PassengerResponseDTO> findByKeycloakSub(@NonNull String keycloakSub) {
        return passengerRepository.findByKeycloakSubAndIsActiveTrue(keycloakSub)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("passenger", "keycloakSub", keycloakSub))
        )
        .map(passengerMapper::toDto);
    }


    @Override
    public Flux<PassengerResponseDTO> findAll() {
        return passengerRepository.findAllByIsActiveTrue()
        .map(passengerMapper::toDto);
    }

}
