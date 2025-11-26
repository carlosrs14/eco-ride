package com.bloque3.trip_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bloque3.trip_service.controllers.dto.response.LocationResponse;
import com.bloque3.trip_service.exceptions.ResourceNotFoundException;
import com.bloque3.trip_service.mappers.LocationMapper;
import com.bloque3.trip_service.models.trip.Location;
import com.bloque3.trip_service.repositories.LocationRepository;
import com.bloque3.trip_service.services.impl.LocationServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void findById_shouldReturnLocation_whenExists() {
        UUID locationId = UUID.randomUUID();
        Location location = Location.builder().id(locationId).name("Test Location").build();
        LocationResponse responseDto = new LocationResponse(locationId.toString(), "Test Location", null);

        when(locationRepository.findActiveById(locationId)).thenReturn(Mono.just(location));
        when(locationMapper.toDto(location)).thenReturn(responseDto);

        StepVerifier.create(locationService.findById(locationId.toString()))
                .assertNext(response -> assertEquals(locationId.toString(), response.id()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findActiveById(locationId)).thenReturn(Mono.empty());

        StepVerifier.create(locationService.findById(locationId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
