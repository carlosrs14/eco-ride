package com.bloque3.payment_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.payment_service.clients.TripClient;
import com.bloque3.payment_service.controllers.dtos.request.PaymentIntentRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;
import com.bloque3.payment_service.exception.ResourceNotFoundException;
import com.bloque3.payment_service.mappers.PaymentIntentMapper;
import com.bloque3.payment_service.models.PaymentIntent;
import com.bloque3.payment_service.repositories.PaymentIntentRepository;
import com.bloque3.payment_service.services.PaymentIntentService;

import lombok.NonNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class PaymentIntentServiceImpl implements PaymentIntentService {

  private final PaymentIntentRepository paymentIntentRepository;
  private final PaymentIntentMapper paymentIntentMapper;
  private final TripClient tripClient;

  public PaymentIntentServiceImpl(PaymentIntentMapper paymentIntentMapper,
      PaymentIntentRepository paymentIntentRepository, TripClient tripClient) {
    this.paymentIntentMapper = paymentIntentMapper;
    this.paymentIntentRepository = paymentIntentRepository;
    this.tripClient = tripClient;
  }

  @Override
  public Mono<PaymentIntentResponseDTO> create(PaymentIntentRequestDTO paymentIntentRequest) {

    return Mono.fromCallable(() -> tripClient.getReservationById(paymentIntentRequest.reservationId()))
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(reserva -> {

                PaymentIntent paymentIntent = paymentIntentMapper.toEntity(paymentIntentRequest);
                paymentIntent.setStatus(5);
                paymentIntent.setCreatedAt(Instant.now());
                paymentIntent.setUpdatedAt(Instant.now());

                return paymentIntentRepository.save(paymentIntent);
            })
            .map(paymentIntentMapper::toDto);
  }

  @Override
  public Mono<PaymentIntentResponseDTO> updateStatus(@NonNull String id, @NonNull Integer statusId) {

    UUID uuid = UUID.fromString(id);
    return paymentIntentRepository.findByIdAndIsActiveTrue(uuid)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("payment", "id", id)))
        .flatMap(existingPaymentIntent -> {
          PaymentIntent paymentIntent = PaymentIntent.builder()
              .id(uuid)
              .status(statusId)
              .updatedAt(Instant.now())
              .build();
          if (paymentIntent == null) throw new NullPointerException("Payment intent is null");
          return paymentIntentRepository.save(paymentIntent).map(paymentIntentMapper::toDto);
        });

  }

  @Override
  public Mono<PaymentIntentResponseDTO> findById(String id) {
    UUID uuid = UUID.fromString(id);
    return paymentIntentRepository.findByIdAndIsActiveTrue(uuid)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("payment", "id", uuid)))
        .map(paymentIntentMapper::toDto);
  }

}
