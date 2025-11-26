package com.bloque3.payment_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bloque3.payment_service.controllers.dtos.request.ChargeRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;
import com.bloque3.payment_service.messages.events.PaymentAuthorizedEvent;
import com.bloque3.payment_service.messages.producers.PaymentAuthorizedProducer;
import com.bloque3.payment_service.services.ChargeService;
import com.bloque3.payment_service.services.PaymentIntentService;
import com.bloque3.payment_service.services.RefundService;

import reactor.core.publisher.Mono;

@WebFluxTest(PaymentIntentController.class)
public class PaymentIntentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PaymentIntentService paymentIntentService;

    @MockitoBean
    private ChargeService chargeService;

    @MockitoBean
    private RefundService refundService;

    @MockitoBean
    private PaymentAuthorizedProducer paymentAuthorizedProducer;

    @SuppressWarnings("null")
	@Test
    void createCharge_shouldReturnCreatedCharge_whenValidRequest() {
        UUID paymentIntentId = UUID.randomUUID();
        ChargeRequestDTO request = ChargeRequestDTO.builder().provider("stripe").providerRef("ref123").build();
        ChargeResponseDTO response = ChargeResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .paymentIntentId(paymentIntentId.toString())
                .provider(request.provider())
                .providerRef(request.providerRef())
                .build();
        PaymentIntentResponseDTO paymentIntentResponse = PaymentIntentResponseDTO.builder()
                .reservationId(UUID.randomUUID().toString())
                .build();

        when(chargeService.create(any(String.class), any(ChargeRequestDTO.class))).thenReturn(Mono.just(response));
        when(paymentIntentService.updateStatus(any(String.class), any(Integer.class))).thenReturn(Mono.just(paymentIntentResponse));
        when(paymentAuthorizedProducer.sendMessage(any(PaymentAuthorizedEvent.class))).thenReturn(true);


        webTestClient.post().uri("/payments/capture/{intentId}", paymentIntentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChargeResponseDTO.class)
                .isEqualTo(response);
    }

    @SuppressWarnings("null")
	@Test
    void createCharge_shouldReturnBadRequest_whenValidationError() {
        UUID paymentIntentId = UUID.randomUUID();
        ChargeRequestDTO request = ChargeRequestDTO.builder().build(); // Invalid request

        webTestClient.post().uri("/payments/capture/{intentId}", paymentIntentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
