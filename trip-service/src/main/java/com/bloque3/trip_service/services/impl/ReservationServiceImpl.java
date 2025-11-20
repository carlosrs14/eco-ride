package com.bloque3.trip_service.services.impl;

import java.util.UUID;

import com.bloque3.trip_service.controllers.dto.request.ReservationRequest;
import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;
import com.bloque3.trip_service.exceptions.ResourceNotFoundException;
import com.bloque3.trip_service.mappers.ReservationMapper;
import com.bloque3.trip_service.repositories.ReservationRepository;
import com.bloque3.trip_service.services.ReservationService;

import io.micrometer.common.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;


    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper; 
    }


    @Override
    public Mono<ReservationResponse> create(ReservationRequest reservationRequest) {
        if (reservationRequest.tripId() == null || reservationRequest.passengerId() == null) {
            throw new IllegalArgumentException("tripId and passengerId are required");
        }
        
        return reservationRepository.save(reservationMapper.toEntity(reservationRequest))
            .map(reservationMapper::toDto);
    }


    @Override
    public Mono<ReservationResponse> findById(String id) {
        UUID uuid = UUID.fromString(id);
        return reservationRepository.findActiveByid(uuid)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("reservation", "id", id))
        )
        .map(reservationMapper::toDto); 

    }

    @Override
    public Flux<ReservationResponse> findByTripId(String tripId) {
        UUID uuid = UUID.fromString(tripId);
        return reservationRepository.findByTripId(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("tripId", "id", tripId))
            )
            .map(reservationMapper::toDto);
    }

    @Override
    public Flux<ReservationResponse> findByPassengerId(String passengerId) {
        UUID uuid = UUID.fromString(passengerId);
        return reservationRepository.findByPassengerId(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("passengerId", "id", passengerId))
            )
            .map(reservationMapper::toDto);
    }

    @Override
    public Mono<ReservationResponse> update(@NonNull String id, ReservationRequest reservationRequest) {
                // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
        
    }

    @Override
    public Mono<Void> delete(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    
}
