package com.bloque3.trip_service.services.impl;

import java.util.UUID;

import com.bloque3.trip_service.controllers.dto.response.LocationResponse;
import com.bloque3.trip_service.exceptions.ResourceNotFoundException;
import com.bloque3.trip_service.mappers.LocationMapper;
import com.bloque3.trip_service.repositories.LocationRepository;
import com.bloque3.trip_service.services.LocationService;

import reactor.core.publisher.Mono;

public class LocationServiceImpl implements LocationService{
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public Mono<LocationResponse> findById(String id) {
        UUID uuid = UUID.fromString(id);
        return locationRepository.findActiveById(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("location", "id", id))
            )
            .map(locationMapper::toDto);
    }
    
}
