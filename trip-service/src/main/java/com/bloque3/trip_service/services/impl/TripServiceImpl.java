package com.bloque3.trip_service.services.impl;


import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.trip_service.clients.CarClient;
import com.bloque3.trip_service.clients.DriverClient;
import com.bloque3.trip_service.clients.dto.CarResponse;
import com.bloque3.trip_service.clients.dto.DriverResponse;
import com.bloque3.trip_service.controllers.dto.request.TripRequest;
import com.bloque3.trip_service.controllers.dto.response.LocationResponse;
import com.bloque3.trip_service.controllers.dto.response.TripResponse;
import com.bloque3.trip_service.exceptions.ResourceNotFoundException;
import com.bloque3.trip_service.mappers.TripMapper;
import com.bloque3.trip_service.models.trip.Trip;
import com.bloque3.trip_service.repositories.TripRepository;
import com.bloque3.trip_service.services.LocationService;
import com.bloque3.trip_service.services.TripService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final DriverClient driverClient;
    private final CarClient carClient;
    private final LocationService locationService;
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public Mono<TripResponse> create(TripRequest tripRequest) {
        Mono<CarResponse> carResponseMono = Mono.fromCallable(() ->carClient.getCarById(tripRequest.carId()))
                    .subscribeOn(Schedulers.boundedElastic())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("car", "id", tripRequest.carId())));

        Mono<DriverResponse> driverResponseMono = Mono.fromCallable(() ->driverClient.getDriverById(tripRequest.driverId()))
                    .subscribeOn(Schedulers.boundedElastic())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("driverId", "id", tripRequest.driverId()))); 

        Mono<LocationResponse> origenResponseMono = locationService.findById(tripRequest.originId());
        Mono<LocationResponse> destinoResponseMono = locationService.findById(tripRequest.destinationId());

        return Mono.zip(carResponseMono, driverResponseMono, origenResponseMono, destinoResponseMono)
            .flatMap(result -> {
                Trip trip = tripMapper.toEntity(tripRequest);
                trip.setIsActive(true);
                trip.setCreatedAt(Instant.now()); 
                trip.setUpdatedAt(Instant.now());  
                return tripRepository.save(trip);
            })
            .map(tripMapper::toDto);    
    } 

    @Override
    public Mono<TripResponse> findById(String id) {
        UUID uuid = UUID.fromString(id);
        return tripRepository.findActiveById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("trip", "id", id))
            )
            .map(tripMapper::toDto);
    }

    @Override
    public Flux<TripResponse> searchTrips(String originId, String destinationId) {
        UUID originUUID = UUID.fromString(originId);
        UUID destinationUUID = UUID.fromString(destinationId);

        return tripRepository.searchTrips(originUUID, destinationUUID)
                .map(tripMapper::toDto);
    }

    @Override
    public Mono<TripResponse> update(String id, TripRequest tripRequest) {
        UUID uuid = UUID.fromString(id);
        return tripRepository.findActiveById(uuid)  
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("trip", "id", id))
        )
        .flatMap(existingTrip -> {
            Trip trip = tripMapper.toEntity(tripRequest);
            trip.setId(uuid);
            trip.setUpdatedAt(Instant.now());
            return tripRepository.save(trip);
        })
        .map(tripMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        UUID uuid = UUID.fromString(id);
        return tripRepository.findActiveById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("trip", "id", id))
            )
            .flatMap(trip -> {
                trip.setIsActive(false);
                trip.setUpdatedAt(Instant.now());
                return tripRepository.save(trip);
            })
            .then();
    }

    @Override
    public Flux<TripResponse> findByDriverId(String driverId) {
        UUID uuid = UUID.fromString(driverId);
        return tripRepository.findActiveByDriverId(uuid).map(tripMapper::toDto);
    }
}
