package com.bloque3.payment_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.bloque3.payment_service.controllers.dtos.request.ChargeRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;
import com.bloque3.payment_service.exception.ResourceNotFoundException;
import com.bloque3.payment_service.mappers.ChargeMapper;
import com.bloque3.payment_service.models.Charge;
import com.bloque3.payment_service.repositories.ChargeRepository;
import com.bloque3.payment_service.services.impl.ChargeServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ChargeServiceTest {

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private PaymentIntentService paymentIntentService;

    @Mock
    private ChargeMapper chargeMapper;

    @InjectMocks
    private ChargeServiceImpl chargeService;

    @Captor
    private ArgumentCaptor<Charge> chargeCaptor;

    @SuppressWarnings("null")
    @Test
    void create_shouldCreateCharge_whenPaymentIntentExists() {
        UUID paymentIntentId = UUID.randomUUID();
        ChargeRequestDTO request = ChargeRequestDTO.builder().provider("stripe").providerRef("ref123").build();
        PaymentIntentResponseDTO paymentIntentResponse = PaymentIntentResponseDTO.builder().id(paymentIntentId.toString()).build();
        Charge charge = Charge.builder().build();
        Charge savedCharge = Charge.builder().id(UUID.randomUUID()).build();
        ChargeResponseDTO responseDto = ChargeResponseDTO.builder().id(savedCharge.getId().toString()).build();

        when(paymentIntentService.findById(paymentIntentId.toString())).thenReturn(Mono.just(paymentIntentResponse));
        when(chargeMapper.toEntity(request)).thenReturn(charge);
        when(chargeRepository.save(any(Charge.class))).thenReturn(Mono.just(savedCharge));
        when(chargeMapper.toDto(savedCharge)).thenReturn(responseDto);

        StepVerifier.create(chargeService.create(paymentIntentId.toString(), request))
                .assertNext(response -> assertNotNull(response.id()))
                .verifyComplete();

        verify(chargeRepository).save(chargeCaptor.capture());
        Charge capturedCharge = chargeCaptor.getValue();
        assertEquals(paymentIntentId, capturedCharge.getPaymentIntentId());
        assertNotNull(capturedCharge.getCapturedAt());
        assertTrue(capturedCharge.getIsActive());
    }

    @Test
    void create_shouldThrowException_whenPaymentIntentNotFound() {
        UUID paymentIntentId = UUID.randomUUID();
        ChargeRequestDTO request = ChargeRequestDTO.builder().provider("stripe").providerRef("ref123").build();

        when(paymentIntentService.findById(paymentIntentId.toString())).thenReturn(Mono.error(new ResourceNotFoundException("payment", "id", paymentIntentId)));

        StepVerifier.create(chargeService.create(paymentIntentId.toString(), request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void create_shouldThrowException_whenPaymentIntentIdIsInvalid() {
        String invalidId = "invalid-uuid";
        ChargeRequestDTO request = ChargeRequestDTO.builder().provider("stripe").providerRef("ref123").build();

        when(paymentIntentService.findById(invalidId)).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(chargeService.create(invalidId, request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findById_shouldReturnCharge_whenExists() {
        UUID chargeId = UUID.randomUUID();
        Charge charge = Charge.builder().id(chargeId).build();
        ChargeResponseDTO responseDto = ChargeResponseDTO.builder().id(chargeId.toString()).build();

        when(chargeRepository.findByIdAndIsActiveTrue(chargeId)).thenReturn(Mono.just(charge));
        when(chargeMapper.toDto(charge)).thenReturn(responseDto);

        StepVerifier.create(chargeService.findById(chargeId.toString()))
                .assertNext(response -> assertEquals(chargeId.toString(), response.id()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID chargeId = UUID.randomUUID();
        when(chargeRepository.findByIdAndIsActiveTrue(chargeId)).thenReturn(Mono.empty());

        StepVerifier.create(chargeService.findById(chargeId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
