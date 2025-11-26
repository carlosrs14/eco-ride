package com.bloque3.payment_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.bloque3.payment_service.clients.TripClient;
import com.bloque3.payment_service.clients.dtos.ReservationResponseDTO;
import com.bloque3.payment_service.controllers.dtos.request.PaymentIntentRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;
import com.bloque3.payment_service.exception.ResourceNotFoundException;
import com.bloque3.payment_service.mappers.PaymentIntentMapper;
import com.bloque3.payment_service.models.PaymentIntent;
import com.bloque3.payment_service.repositories.PaymentIntentRepository;
import com.bloque3.payment_service.services.impl.PaymentIntentServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PaymentIntentServiceTest {

    @Mock
    private PaymentIntentRepository paymentIntentRepository;

    @Mock
    private PaymentIntentMapper paymentIntentMapper;

    @Mock
    private TripClient tripClient;

    @InjectMocks
    private PaymentIntentServiceImpl paymentIntentService;

    @Captor
    private ArgumentCaptor<PaymentIntent> paymentIntentCaptor;

    @SuppressWarnings("null")
@Test
    void create_shouldCreatePaymentIntent_whenReservationExists() {
        UUID reservationId = UUID.randomUUID();
        PaymentIntentRequestDTO request = PaymentIntentRequestDTO.builder()
                .reservationId(reservationId.toString())
                .amount(100.0)
                .currency("USD")
                .build();
        ReservationResponseDTO reservationResponse = new ReservationResponseDTO(reservationId.toString(), null, null, null);
        PaymentIntent paymentIntent = PaymentIntent.builder().build();
        PaymentIntent savedPaymentIntent = PaymentIntent.builder().id(UUID.randomUUID()).build();
        PaymentIntentResponseDTO responseDto = PaymentIntentResponseDTO.builder().id(savedPaymentIntent.getId().toString()).build();

        when(tripClient.getReservationById(reservationId.toString())).thenReturn(reservationResponse);
        when(paymentIntentMapper.toEntity(request)).thenReturn(paymentIntent);
        when(paymentIntentRepository.save(any(PaymentIntent.class))).thenReturn(Mono.just(savedPaymentIntent));
        when(paymentIntentMapper.toDto(savedPaymentIntent)).thenReturn(responseDto);

        StepVerifier.create(paymentIntentService.create(request))
                .assertNext(response -> assertNotNull(response.id()))
                .verifyComplete();

        verify(paymentIntentRepository).save(paymentIntentCaptor.capture());
        PaymentIntent capturedPaymentIntent = paymentIntentCaptor.getValue();
        assertEquals(5, capturedPaymentIntent.getStatus());
        assertNotNull(capturedPaymentIntent.getCreatedAt());
        assertNotNull(capturedPaymentIntent.getUpdatedAt());
    }

    @Test
    void create_shouldThrowException_whenReservationNotFound() {
        UUID reservationId = UUID.randomUUID();
        PaymentIntentRequestDTO request = PaymentIntentRequestDTO.builder()
                .reservationId(reservationId.toString())
                .amount(100.0)
                .currency("USD")
                .build();
        when(tripClient.getReservationById(reservationId.toString())).thenThrow(new ResourceNotFoundException("reservation", "id", reservationId.toString()));

        StepVerifier.create(paymentIntentService.create(request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @SuppressWarnings("null")
@Test
    void updateStatus_shouldUpdateStatus_whenPaymentIntentExists() {
        UUID paymentIntentId = UUID.randomUUID();
        Integer newStatus = 2;
        PaymentIntent existingPaymentIntent = PaymentIntent.builder().id(paymentIntentId).status(1).build();
        PaymentIntent updatedPaymentIntent = PaymentIntent.builder().id(paymentIntentId).status(newStatus).build();
        PaymentIntentResponseDTO responseDto = PaymentIntentResponseDTO.builder().id(paymentIntentId.toString()).status(newStatus.toString()).build();

        when(paymentIntentRepository.findByIdAndIsActiveTrue(paymentIntentId)).thenReturn(Mono.just(existingPaymentIntent));
        when(paymentIntentRepository.save(any(PaymentIntent.class))).thenReturn(Mono.just(updatedPaymentIntent));
        when(paymentIntentMapper.toDto(updatedPaymentIntent)).thenReturn(responseDto);

        StepVerifier.create(paymentIntentService.updateStatus(paymentIntentId.toString(), newStatus))
                .assertNext(response -> assertEquals(newStatus.toString(), response.status()))
                .verifyComplete();
        
        verify(paymentIntentRepository).save(paymentIntentCaptor.capture());
        PaymentIntent capturedPaymentIntent = paymentIntentCaptor.getValue();
        assertEquals(newStatus, capturedPaymentIntent.getStatus());
        assertNotNull(capturedPaymentIntent.getUpdatedAt());
    }

    @Test
    void updateStatus_shouldThrowException_whenPaymentIntentNotFound() {
        UUID paymentIntentId = UUID.randomUUID();
        Integer newStatus = 2;
        when(paymentIntentRepository.findByIdAndIsActiveTrue(paymentIntentId)).thenReturn(Mono.empty());

        StepVerifier.create(paymentIntentService.updateStatus(paymentIntentId.toString(), newStatus))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findById_shouldReturnPaymentIntent_whenExists() {
        UUID paymentIntentId = UUID.randomUUID();
        PaymentIntent paymentIntent = PaymentIntent.builder().id(paymentIntentId).build();
        PaymentIntentResponseDTO responseDto = PaymentIntentResponseDTO.builder().id(paymentIntentId.toString()).build();

        when(paymentIntentRepository.findByIdAndIsActiveTrue(paymentIntentId)).thenReturn(Mono.just(paymentIntent));
        when(paymentIntentMapper.toDto(paymentIntent)).thenReturn(responseDto);

        StepVerifier.create(paymentIntentService.findById(paymentIntentId.toString()))
                .assertNext(response -> assertEquals(paymentIntentId.toString(), response.id()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID paymentIntentId = UUID.randomUUID();
        when(paymentIntentRepository.findByIdAndIsActiveTrue(paymentIntentId)).thenReturn(Mono.empty());

        StepVerifier.create(paymentIntentService.findById(paymentIntentId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
