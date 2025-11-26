package com.bloque3.trip_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.bloque3.trip_service.clients.PassengerClient;
import com.bloque3.trip_service.controllers.dto.request.ReservationRequest;
import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;
import com.bloque3.trip_service.exceptions.ResourceNotFoundException;
import com.bloque3.trip_service.mappers.ReservationMapper;
import com.bloque3.trip_service.models.outbox.OutboxEvent;
import com.bloque3.trip_service.models.reservation.Reservation;
import com.bloque3.trip_service.repositories.OutboxRepository;
import com.bloque3.trip_service.repositories.ReservationRepository;
import com.bloque3.trip_service.services.impl.ReservationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private PassengerClient passengerClient;

    @Mock
    private TripService tripService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Captor
    private ArgumentCaptor<Reservation> reservationCaptor;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxEventCaptor;

    @Test
    void findById_shouldReturnReservation_whenExists() {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.builder().id(reservationId).build();
        ReservationResponse responseDto = new ReservationResponse(reservationId.toString(), null, null, null, null);

        when(reservationRepository.findActiveByid(reservationId)).thenReturn(Mono.just(reservation));
        when(reservationMapper.toDto(reservation)).thenReturn(responseDto);

        StepVerifier.create(reservationService.findById(reservationId.toString()))
                .assertNext(response -> assertEquals(reservationId.toString(), response.id()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID reservationId = UUID.randomUUID();
        when(reservationRepository.findActiveByid(reservationId)).thenReturn(Mono.empty());

        StepVerifier.create(reservationService.findById(reservationId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findByTripId_shouldReturnReservations_whenExists() {
        UUID tripId = UUID.randomUUID();
        Reservation res1 = Reservation.builder().id(UUID.randomUUID()).build();
        Reservation res2 = Reservation.builder().id(UUID.randomUUID()).build();
        ReservationResponse dto1 = new ReservationResponse(res1.getId().toString(), null, null, null, null);
        ReservationResponse dto2 = new ReservationResponse(res2.getId().toString(), null, null, null, null);

        when(reservationRepository.findByTripId(tripId)).thenReturn(Flux.just(res1, res2));
        when(reservationMapper.toDto(res1)).thenReturn(dto1);
        when(reservationMapper.toDto(res2)).thenReturn(dto2);

        StepVerifier.create(reservationService.findByTripId(tripId.toString()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByTripId_shouldThrowException_whenNotFound() {
        UUID tripId = UUID.randomUUID();
        when(reservationRepository.findByTripId(tripId)).thenReturn(Flux.empty());

        StepVerifier.create(reservationService.findByTripId(tripId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findByPassengerId_shouldReturnReservations_whenExists() {
        UUID passengerId = UUID.randomUUID();
        Reservation res1 = Reservation.builder().id(UUID.randomUUID()).build();
        Reservation res2 = Reservation.builder().id(UUID.randomUUID()).build();
        ReservationResponse dto1 = new ReservationResponse(res1.getId().toString(), null, null, null, null);
        ReservationResponse dto2 = new ReservationResponse(res2.getId().toString(), null, null, null, null);

        when(reservationRepository.findByPassengerId(passengerId)).thenReturn(Flux.just(res1, res2));
        when(reservationMapper.toDto(res1)).thenReturn(dto1);
        when(reservationMapper.toDto(res2)).thenReturn(dto2);

        StepVerifier.create(reservationService.findByPassengerId(passengerId.toString()))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByPassengerId_shouldThrowException_whenNotFound() {
        UUID passengerId = UUID.randomUUID();
        when(reservationRepository.findByPassengerId(passengerId)).thenReturn(Flux.empty());

        StepVerifier.create(reservationService.findByPassengerId(passengerId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldUpdateReservation_whenExists() {
        UUID reservationId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 3);
        Reservation existingReservation = Reservation.builder().id(reservationId).build();
        Reservation updatedReservation = Reservation.builder().id(reservationId).seatsReserved(3).build();
        ReservationResponse responseDto = new ReservationResponse(reservationId.toString(), null, null, null, 3);

        when(reservationRepository.findActiveByid(reservationId)).thenReturn(Mono.just(existingReservation));
        when(reservationMapper.toEntity(request)).thenReturn(updatedReservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(Mono.just(updatedReservation));
        when(reservationMapper.toDto(updatedReservation)).thenReturn(responseDto);

        StepVerifier.create(reservationService.update(reservationId.toString(), request))
                .assertNext(response -> assertEquals(3, response.seatsReserved()))
                .verifyComplete();
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID reservationId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 3);
        when(reservationRepository.findActiveByid(reservationId)).thenReturn(Mono.empty());

        StepVerifier.create(reservationService.update(reservationId.toString(), request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @SuppressWarnings("null")
    @Test
    void delete_shouldDeactivateReservation_whenExists() {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.builder().id(reservationId).isActive(true).build();

        when(reservationRepository.findActiveByid(reservationId)).thenReturn(Mono.just(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(Mono.just(reservation));

        StepVerifier.create(reservationService.delete(reservationId.toString()))
                .verifyComplete();

        verify(reservationRepository).save(reservationCaptor.capture());
        Reservation captured = reservationCaptor.getValue();
        assertEquals(reservationId, captured.getId());
        assertFalse(captured.getIsActive());
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID reservationId = UUID.randomUUID();
        when(reservationRepository.findActiveByid(reservationId)).thenReturn(Mono.empty());

        StepVerifier.create(reservationService.delete(reservationId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
