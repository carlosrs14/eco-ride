package com.bloque3.trip_service.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.bloque3.trip_service.clients.PassengerClient;
import com.bloque3.trip_service.clients.dto.PassengerResponse;
import com.bloque3.trip_service.controllers.dto.request.ReservationRequest;
import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;
import com.bloque3.trip_service.controllers.dto.response.TripResponse;
import com.bloque3.trip_service.exceptions.MessageQueueException;
import com.bloque3.trip_service.exceptions.ResourceNotFoundException;
import com.bloque3.trip_service.mappers.ReservationMapper;
import com.bloque3.trip_service.messages.events.ReservationRequestEvent;
import com.bloque3.trip_service.models.outbox.OutboxEvent;
import com.bloque3.trip_service.models.reservation.Reservation;
import com.bloque3.trip_service.repositories.OutboxRepository;
import com.bloque3.trip_service.repositories.ReservationRepository;
import com.bloque3.trip_service.services.ReservationService;
import com.bloque3.trip_service.services.TripService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final PassengerClient passengerClient;
    private final TripService tripService;
    private final TransactionalOperator transactionalOperator;
    private final OutboxRepository outboxRepository;

    @Override
    public Mono<ReservationResponse> create(ReservationRequest reservationRequest) {

        Mono<PassengerResponse> passengerResponseMono = Mono.fromCallable(() ->passengerClient.getPassengerById(reservationRequest.passengerId()))
                    .subscribeOn(Schedulers.boundedElastic())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("passenger", "id", reservationRequest.passengerId())));

        Mono<TripResponse> tripResponseMono = tripService.findById(reservationRequest.tripId());

        return Mono.zip(passengerResponseMono, tripResponseMono)
            .flatMap(result -> {
                Reservation reservation = reservationMapper.toEntity(reservationRequest);
                if (reservation.getId() == null) reservation.setId(UUID.randomUUID());
                reservation.setIsActive(true);
                reservation.setCreatedAt(Instant.now());
                reservation.setUpdatedAt(Instant.now());

                BigDecimal amount = result.getT2().pricePerSeat().multiply(new BigDecimal(reservationRequest.seatsReserved()));
                reservation.setAmount(amount);

                ReservationRequestEvent event = ReservationRequestEvent
                    .builder()
                    .id(reservation.getId())
                    .tripId(reservation.getTripId())
                    .passengerId(reservation.getPassengerId())
                    .amount(amount)
                    .sagaId(UUID.randomUUID())
                    .messageId(UUID.randomUUID())
                    .build();

                String payloadJson;
                try {
                    payloadJson = new ObjectMapper().writeValueAsString(event);
                } catch (Exception ex) {
                    return Mono.error(ex);
                }

                OutboxEvent outbox = OutboxEvent
                    .builder()
                    .aggregateType("Reservation")
                    .aggregateId(reservation.getId())
                    .eventType(event.eventType())
                    .payload(payloadJson)
                    .messageId(event.messageId())
                    .sagaId(event.sagaId())
                    .status("PENDING")
                    .createdAt(Instant.now())
                    .retryCount(0)
                    .build();
                if (outbox == null) throw new MessageQueueException("Outbox is null");
                
                return transactionalOperator.execute(
                    tx -> reservationRepository.save(reservation)
                        .flatMap(saved -> outboxRepository.save(outbox).thenReturn(saved))
                ).single();

            }).map(reservationMapper::toDto);
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
        UUID uuid = UUID.fromString(id);
        return reservationRepository.findActiveByid(uuid)   
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("reservation", "id", id))
            )
            .flatMap(existingReservation -> {
                Reservation reservation = reservationMapper.toEntity(reservationRequest);
                reservation.setId(uuid);
                return reservationRepository.save(reservation);
            }).map(reservationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        UUID uuid = UUID.fromString(id);
        return reservationRepository.findActiveByid(uuid)
            .switchIfEmpty(
                Mono.error(new ResourceNotFoundException("reservation", "id", id))
            ).flatMap(reservation -> {
                reservation.setIsActive(false);
                return reservationRepository.save(reservation);
            }).then();
    }
}
