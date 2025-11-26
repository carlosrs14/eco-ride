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

import com.bloque3.payment_service.controllers.dtos.request.RefundRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;
import com.bloque3.payment_service.controllers.dtos.response.RefundResponseDTO;
import com.bloque3.payment_service.exception.ResourceNotFoundException;
import com.bloque3.payment_service.mappers.RefundMapper;
import com.bloque3.payment_service.models.Refund;
import com.bloque3.payment_service.repositories.RefundRepository;
import com.bloque3.payment_service.services.impl.RefundServiceImpl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private ChargeService chargeService;

    @Mock
    private RefundMapper refundMapper;

    @InjectMocks
    private RefundServiceImpl refundService;

    @Captor
    private ArgumentCaptor<Refund> refundCaptor;

    @SuppressWarnings("null")
    @Test
    void create_shouldCreateRefund_whenChargeExists() {
        UUID chargeId = UUID.randomUUID();
        RefundRequestDTO request = RefundRequestDTO.builder().amount(50.0).build();
        ChargeResponseDTO chargeResponse = ChargeResponseDTO.builder().id(chargeId.toString()).build();
        Refund refund = Refund.builder().build();
        Refund savedRefund = Refund.builder().id(UUID.randomUUID()).chargeId(chargeId).build();
        RefundResponseDTO responseDto = RefundResponseDTO.builder().id(savedRefund.getId().toString()).chargeId(chargeId.toString()).build();

        when(chargeService.findById(chargeId.toString())).thenReturn(Mono.just(chargeResponse));
        when(refundMapper.toEntity(request)).thenReturn(refund);
        when(refundRepository.save(any(Refund.class))).thenReturn(Mono.just(savedRefund));
        when(refundMapper.toDto(savedRefund)).thenReturn(responseDto);

        StepVerifier.create(refundService.create(chargeId.toString(), request))
                .assertNext(response -> assertNotNull(response.id()))
                .verifyComplete();

        verify(refundRepository).save(refundCaptor.capture());
        Refund capturedRefund = refundCaptor.getValue();
        assertEquals(chargeId, capturedRefund.getChargeId());
        assertNotNull(capturedRefund.getCreatedAt());
    }

    @Test
    void create_shouldThrowException_whenChargeNotFound() {
        UUID chargeId = UUID.randomUUID();
        RefundRequestDTO request = RefundRequestDTO.builder().amount(50.0).build();

        when(chargeService.findById(chargeId.toString())).thenReturn(Mono.error(new ResourceNotFoundException("charge", "id", chargeId)));

        StepVerifier.create(refundService.create(chargeId.toString(), request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void create_shouldThrowException_whenChargeIdIsInvalid() {
        String invalidId = "invalid-uuid";
        RefundRequestDTO request = RefundRequestDTO.builder().amount(50.0).build();

        when(chargeService.findById(invalidId)).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(refundService.create(invalidId, request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
