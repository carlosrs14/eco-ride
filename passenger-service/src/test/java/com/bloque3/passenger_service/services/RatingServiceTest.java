package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.clients.TripClient;
import com.bloque3.passenger_service.clients.dto.TripResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.response.RatingResponseDTO;
import com.bloque3.passenger_service.exceptions.ResourceNotFoundException;
import com.bloque3.passenger_service.mappers.RatingMapper;
import com.bloque3.passenger_service.models.Rating;
import com.bloque3.passenger_service.repositories.RatingRepository;
import com.bloque3.passenger_service.services.impl.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private TripClient tripClient;
    
    @Mock
    private DriverService driverService;

    @Mock
    private PassengerService passengerService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @SuppressWarnings("null")
    @Test
    void create_shouldCreateRating_whenAllDataIsValid() {
        UUID tripId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        RatingRequestDTO request = new RatingRequestDTO(tripId.toString(), fromId.toString(), toId.toString(), 5f, "Excellent");

        TripResponseDTO tripResponse = new TripResponseDTO(tripId.toString(), 0, null, null, null, null, null, null);
        DriverResponseDTO driverResponse = new DriverResponseDTO(fromId.toString(), null, null, null);
        PassengerResponseDTO passengerResponse = new PassengerResponseDTO(toId.toString(), null, null, null, null);
        
        Rating ratingEntity = Rating.builder().build();
        Rating savedRating = Rating.builder().id(UUID.randomUUID()).build();
        RatingResponseDTO ratingResponse = new RatingResponseDTO(savedRating.getId().toString(), tripId.toString(), fromId.toString(), toId.toString(), 5f, "Excellent");

        when(tripClient.getTripById(tripId.toString())).thenReturn(tripResponse);
        when(driverService.findById(fromId.toString())).thenReturn(Mono.just(driverResponse));
        when(passengerService.findById(toId.toString())).thenReturn(Mono.just(passengerResponse));
        when(ratingMapper.toEntity(any(RatingRequestDTO.class))).thenReturn(ratingEntity);
        when(ratingRepository.save(any(Rating.class))).thenReturn(Mono.just(savedRating));
        when(ratingMapper.toDto(savedRating)).thenReturn(ratingResponse);

        StepVerifier.create(ratingService.create(request))
                .expectNext(ratingResponse)
                .verifyComplete();
    }
    
    @Test
    void create_shouldThrowException_whenDriverNotFound() {
        UUID tripId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        RatingRequestDTO request = new RatingRequestDTO(tripId.toString(), fromId.toString(), toId.toString(), 5f, "Excellent");

        TripResponseDTO tripResponse = new TripResponseDTO(tripId.toString(), 0, null, null, null, null, null, null);
        
        when(tripClient.getTripById(tripId.toString())).thenReturn(tripResponse);
        when(driverService.findById(fromId.toString())).thenReturn(Mono.error(new ResourceNotFoundException("driver", "id", fromId.toString())));
        when(passengerService.findById(toId.toString())).thenReturn(Mono.just(new PassengerResponseDTO(toId.toString(), null, null, null, null)));


        StepVerifier.create(ratingService.create(request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findById_shouldReturnRating_whenExists() {
        UUID ratingId = UUID.randomUUID();
        Rating rating = Rating.builder().id(ratingId).build();
        RatingResponseDTO responseDto = new RatingResponseDTO(ratingId.toString(), null, null, null, 0f, null);

        when(ratingRepository.findById(ratingId)).thenReturn(Mono.just(rating));
        when(ratingMapper.toDto(rating)).thenReturn(responseDto);

        StepVerifier.create(ratingService.findById(ratingId.toString()))
                .expectNext(responseDto)
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID ratingId = UUID.randomUUID();
        when(ratingRepository.findById(ratingId)).thenReturn(Mono.empty());

        StepVerifier.create(ratingService.findById(ratingId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findAll_shouldReturnAllRatings() {
        Rating rating1 = Rating.builder().id(UUID.randomUUID()).build();
        Rating rating2 = Rating.builder().id(UUID.randomUUID()).build();
        RatingResponseDTO dto1 = new RatingResponseDTO(rating1.getId().toString(), null, null, null, 0f, null);
        RatingResponseDTO dto2 = new RatingResponseDTO(rating2.getId().toString(), null, null, null, 0f, null);

        when(ratingRepository.findAll()).thenReturn(reactor.core.publisher.Flux.just(rating1, rating2));
        when(ratingMapper.toDto(rating1)).thenReturn(dto1);
        when(ratingMapper.toDto(rating2)).thenReturn(dto2);

        StepVerifier.create(ratingService.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoRatings() {
        when(ratingRepository.findAll()).thenReturn(reactor.core.publisher.Flux.empty());

        StepVerifier.create(ratingService.findAll())
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldUpdateRating_whenExists() {
        UUID ratingId = UUID.randomUUID();
        com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO request = new com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO(4f, "Good");
        Rating existingRating = Rating.builder().id(ratingId).build();
        Rating updatedRating = Rating.builder().id(ratingId).score(4f).comment("Good").build();
        RatingResponseDTO responseDto = new RatingResponseDTO(ratingId.toString(), null, null, null, 4f, "Good");

        when(ratingRepository.findById(ratingId)).thenReturn(Mono.just(existingRating));
        when(ratingMapper.toEntity(request)).thenReturn(updatedRating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(Mono.just(updatedRating));
        when(ratingMapper.toDto(updatedRating)).thenReturn(responseDto);

        StepVerifier.create(ratingService.update(ratingId.toString(), request))
                .expectNext(responseDto)
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID ratingId = UUID.randomUUID();
        com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO request = new com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO(4f, "Good");
        when(ratingRepository.findById(ratingId)).thenReturn(Mono.empty());

        StepVerifier.create(ratingService.update(ratingId.toString(), request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findAllByPassengerId_shouldReturnRatings_whenExists() {
        UUID passengerId = UUID.randomUUID();
        Rating rating1 = Rating.builder().id(UUID.randomUUID()).build();
        Rating rating2 = Rating.builder().id(UUID.randomUUID()).build();
        RatingResponseDTO dto1 = new RatingResponseDTO(rating1.getId().toString(), null, null, null, 0f, null);
        RatingResponseDTO dto2 = new RatingResponseDTO(rating2.getId().toString(), null, null, null, 0f, null);

        when(ratingRepository.findAllByFromId(passengerId)).thenReturn(reactor.core.publisher.Flux.just(rating1, rating2));
        when(ratingMapper.toDto(rating1)).thenReturn(dto1);
        when(ratingMapper.toDto(rating2)).thenReturn(dto2);

        StepVerifier.create(ratingService.findAllByPassengerId(passengerId.toString()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAllByPassengerId_shouldReturnEmpty_whenNoRatings() {
        UUID passengerId = UUID.randomUUID();
        when(ratingRepository.findAllByFromId(passengerId)).thenReturn(reactor.core.publisher.Flux.empty());

        StepVerifier.create(ratingService.findAllByPassengerId(passengerId.toString()))
                .verifyComplete();
    }
}
