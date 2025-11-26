package com.bloque3.passenger_service.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.passenger_service.clients.TripClient;
import com.bloque3.passenger_service.clients.dto.TripResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.response.RatingResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO;
import com.bloque3.passenger_service.exceptions.ResourceNotFoundException;
import com.bloque3.passenger_service.mappers.RatingMapper;
import com.bloque3.passenger_service.models.Rating;
import com.bloque3.passenger_service.repositories.RatingRepository;
import com.bloque3.passenger_service.services.DriverService;
import com.bloque3.passenger_service.services.PassengerService;
import com.bloque3.passenger_service.services.RatingService;

import jakarta.validation.Valid;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepositoy;
    private final RatingMapper ratingMapper;
    private final TripClient tripClient;
    private final DriverService driverService;
    private final PassengerService passengerService;

    public RatingServiceImpl(RatingRepository ratingRepositoy, RatingMapper ratingMapper, TripClient tripClient,
        DriverService driverService, PassengerService passengerService) {
        this.ratingRepositoy = ratingRepositoy;
        this.ratingMapper = ratingMapper;
        this.tripClient = tripClient;
        this.driverService = driverService;
        this.passengerService = passengerService;
    }

    @Override
    public Mono<RatingResponseDTO> create(@Valid RatingRequestDTO ratingRequestDTO) {
        Mono<TripResponseDTO> tripMono = Mono.fromCallable(() -> tripClient.getTripById(ratingRequestDTO.tripId()))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("trip", "id", ratingRequestDTO.tripId())))
                .onErrorMap(e -> new ResourceNotFoundException("trip", "id", ratingRequestDTO.tripId()));

        Mono<DriverResponseDTO> driverMono = driverService.findById(ratingRequestDTO.fromId());

        Mono<PassengerResponseDTO> passengerMono = passengerService.findById(ratingRequestDTO.toId());

        return Mono.zip(tripMono, driverMono, passengerMono)
                .flatMap(result -> {
                    Rating rating = ratingMapper.toEntity(ratingRequestDTO);
                    if (rating == null) throw new NullPointerException();

                    return ratingRepositoy.save(rating);
                })
                .map(ratingMapper::toDto);
    }

    @Override
    public Mono<RatingResponseDTO> findById(@NonNull String id) {
        UUID uuid = UUID.fromString(id);
        if (uuid == null) throw new NullPointerException();

        return ratingRepositoy.findById(uuid)
                .switchIfEmpty(
                        Mono.error(new ResourceNotFoundException("rating", "id", id))
                )
                .map(ratingMapper::toDto);
    }

    @Override
    public Mono<RatingResponseDTO> update(String id, RatingRequestUpdateDTO ratingRequestUpdateDTO) {
        UUID uuid = UUID.fromString(id);
        if (uuid == null) throw new NullPointerException();

        return ratingRepositoy.findById(uuid)
                .switchIfEmpty(
                    Mono.error(new ResourceNotFoundException("rating", "id", id))
                ).flatMap(ratin -> {
                    Rating rating = ratingMapper.toEntity(ratingRequestUpdateDTO);
                    rating.setId(uuid);
                    return ratingRepositoy.save(rating);
                }).map(ratingMapper::toDto);
    }

    @Override
    public Flux<RatingResponseDTO> findAllByPassengerId(String passengerId) {
        UUID uuid = UUID.fromString(passengerId);

        return ratingRepositoy.findAllByFromId(uuid)
                .map(ratingMapper::toDto);
    }

    @Override 
    public Flux<RatingResponseDTO> findAll() {
        return ratingRepositoy.findAll()
        .map(
            ratingMapper::toDto
        );
    }   
}
