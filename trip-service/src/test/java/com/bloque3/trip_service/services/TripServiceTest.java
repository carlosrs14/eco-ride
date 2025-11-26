package com.bloque3.trip_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.bloque3.trip_service.services.impl.TripServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private DriverClient driverClient;

    @Mock
    private CarClient carClient;

    @Mock
    private LocationService locationService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private TripServiceImpl tripService;

    @Captor
    private ArgumentCaptor<Trip> tripCaptor;

    @SuppressWarnings("null")
    @Test
    void create_shouldCreateTrip_whenAllDataIsValid() {
        UUID driverId = UUID.randomUUID();
        UUID carId = UUID.randomUUID();
        UUID originId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        TripRequest request = new TripRequest(5, Instant.now().plusSeconds(3600), new BigDecimal("100.00"), originId.toString(), destinationId.toString(), driverId.toString(), carId.toString());

        CarResponse carResponse = new CarResponse(carId.toString(), "plate", "brand", "model", "color", 4, driverId.toString());
        DriverResponse driverResponse = new DriverResponse(driverId.toString(), UUID.randomUUID().toString(), "license", true);
        LocationResponse originResponse = new LocationResponse(originId.toString(), "Origin", UUID.randomUUID().toString());
        LocationResponse destinationResponse = new LocationResponse(destinationId.toString(), "Destination", UUID.randomUUID().toString());
        Trip trip = Trip.builder().build();
        Trip savedTrip = Trip.builder().id(UUID.randomUUID()).build();
        TripResponse responseDto = new TripResponse(savedTrip.getId().toString(), 5, Instant.now().toString(), new BigDecimal("100.00"), originId.toString(), destinationId.toString(), driverId.toString(), carId.toString());

        when(carClient.getCarById(carId.toString())).thenReturn(carResponse);
        when(driverClient.getDriverById(driverId.toString())).thenReturn(driverResponse);
        when(locationService.findById(originId.toString())).thenReturn(Mono.just(originResponse));
        when(locationService.findById(destinationId.toString())).thenReturn(Mono.just(destinationResponse));
        when(tripMapper.toEntity(request)).thenReturn(trip);
        when(tripRepository.save(any(Trip.class))).thenReturn(Mono.just(savedTrip));
        when(tripMapper.toDto(savedTrip)).thenReturn(responseDto);

        StepVerifier.create(tripService.create(request))
                .assertNext(response -> assertNotNull(response.id()))
                .verifyComplete();

        verify(tripRepository).save(tripCaptor.capture());
        Trip capturedTrip = tripCaptor.getValue();
        assertTrue(capturedTrip.getIsActive());
        assertNotNull(capturedTrip.getCreatedAt());
        assertNotNull(capturedTrip.getUpdatedAt());
    }
    
    @Test
    void findById_shouldReturnTrip_whenExists() {
        UUID tripId = UUID.randomUUID();
        Trip trip = Trip.builder().id(tripId).build();
        TripResponse responseDto = new TripResponse(tripId.toString(), null, null, null, null, null, null, null);

        when(tripRepository.findActiveById(tripId)).thenReturn(Mono.just(trip));
        when(tripMapper.toDto(trip)).thenReturn(responseDto);

        StepVerifier.create(tripService.findById(tripId.toString()))
                .assertNext(response -> assertEquals(tripId.toString(), response.id()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID tripId = UUID.randomUUID();
        when(tripRepository.findActiveById(tripId)).thenReturn(Mono.empty());

        StepVerifier.create(tripService.findById(tripId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void searchTrips_shouldReturnTrips_whenExists() {
        UUID originId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        Trip trip1 = Trip.builder().id(UUID.randomUUID()).build();
        Trip trip2 = Trip.builder().id(UUID.randomUUID()).build();
        TripResponse dto1 = new TripResponse(trip1.getId().toString(), null, null, null, null, null, null, null);
        TripResponse dto2 = new TripResponse(trip2.getId().toString(), null, null, null, null, null, null, null);

        when(tripRepository.searchTrips(originId, destinationId)).thenReturn(Flux.just(trip1, trip2));
        when(tripMapper.toDto(trip1)).thenReturn(dto1);
        when(tripMapper.toDto(trip2)).thenReturn(dto2);

        StepVerifier.create(tripService.searchTrips(originId.toString(), destinationId.toString()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void searchTrips_shouldReturnEmpty_whenNoTrips() {
        UUID originId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        when(tripRepository.searchTrips(originId, destinationId)).thenReturn(Flux.empty());

        StepVerifier.create(tripService.searchTrips(originId.toString(), destinationId.toString()))
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldUpdateTrip_whenExists() {
        UUID tripId = UUID.randomUUID();
        TripRequest request = new TripRequest(3, Instant.now().plusSeconds(7200), new BigDecimal("120.00"), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Trip existingTrip = Trip.builder().id(tripId).build();
        Trip updatedTrip = Trip.builder().id(tripId).availableSeats(3).build();
        TripResponse responseDto = new TripResponse(tripId.toString(), 3, null, null, null, null, null, null);

        when(tripRepository.findActiveById(tripId)).thenReturn(Mono.just(existingTrip));
        when(tripMapper.toEntity(request)).thenReturn(updatedTrip);
        when(tripRepository.save(any(Trip.class))).thenReturn(Mono.just(updatedTrip));
        when(tripMapper.toDto(updatedTrip)).thenReturn(responseDto);

        StepVerifier.create(tripService.update(tripId.toString(), request))
                .assertNext(response -> assertEquals(3, response.availableSeats()))
                .verifyComplete();
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID tripId = UUID.randomUUID();
        TripRequest request = new TripRequest(3, Instant.now().plusSeconds(7200), new BigDecimal("120.00"), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(tripRepository.findActiveById(tripId)).thenReturn(Mono.empty());

        StepVerifier.create(tripService.update(tripId.toString(), request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @SuppressWarnings("null")
    @Test
    void delete_shouldDeactivateTrip_whenExists() {
        UUID tripId = UUID.randomUUID();
        Trip trip = Trip.builder().id(tripId).isActive(true).build();

        when(tripRepository.findActiveById(tripId)).thenReturn(Mono.just(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(Mono.just(trip));

        StepVerifier.create(tripService.delete(tripId.toString()))
                .verifyComplete();

        verify(tripRepository).save(tripCaptor.capture());
        Trip captured = tripCaptor.getValue();
        assertEquals(tripId, captured.getId());
        assertEquals(false, captured.getIsActive());
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID tripId = UUID.randomUUID();
        when(tripRepository.findActiveById(tripId)).thenReturn(Mono.empty());

        StepVerifier.create(tripService.delete(tripId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findByDriverId_shouldReturnTrips_whenExists() {
        UUID driverId = UUID.randomUUID();
        Trip trip1 = Trip.builder().id(UUID.randomUUID()).build();
        Trip trip2 = Trip.builder().id(UUID.randomUUID()).build();
        TripResponse dto1 = new TripResponse(trip1.getId().toString(), null, null, null, null, null, null, null);
        TripResponse dto2 = new TripResponse(trip2.getId().toString(), null, null, null, null, null, null, null);

        when(tripRepository.findActiveByDriverId(driverId)).thenReturn(Flux.just(trip1, trip2));
        when(tripMapper.toDto(trip1)).thenReturn(dto1);
        when(tripMapper.toDto(trip2)).thenReturn(dto2);

        StepVerifier.create(tripService.findByDriverId(driverId.toString()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByDriverId_shouldReturnEmpty_whenNoTrips() {
        UUID driverId = UUID.randomUUID();
        when(tripRepository.findActiveByDriverId(driverId)).thenReturn(Flux.empty());

        StepVerifier.create(tripService.findByDriverId(driverId.toString()))
                .verifyComplete();
    }
}
