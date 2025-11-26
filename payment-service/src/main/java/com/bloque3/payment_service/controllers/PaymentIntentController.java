package com.bloque3.payment_service.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.payment_service.controllers.dtos.request.ChargeRequestDTO;
import com.bloque3.payment_service.controllers.dtos.request.PaymentIntentRequestDTO;
import com.bloque3.payment_service.controllers.dtos.request.RefundRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;
import com.bloque3.payment_service.controllers.dtos.response.RefundResponseDTO;
import com.bloque3.payment_service.messages.events.PaymentAuthorizedEvent;
import com.bloque3.payment_service.messages.producers.PaymentAuthorizedProducer;
import com.bloque3.payment_service.services.ChargeService;
import com.bloque3.payment_service.services.PaymentIntentService;
import com.bloque3.payment_service.services.RefundService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/payments")
public class PaymentIntentController {
    private final PaymentIntentService paymentIntentService;
    private final ChargeService chargeService;
    private final RefundService refundServie;
    private final PaymentAuthorizedProducer paymentAuthorizedProducer;

    public PaymentIntentController(PaymentIntentService paymentIntentService, ChargeService chargeService,
            RefundService refundServie, PaymentAuthorizedProducer paymentAuthorizedProducer) {
        this.paymentIntentService = paymentIntentService;
        this.chargeService = chargeService;
        this.refundServie = refundServie;
        this.paymentAuthorizedProducer = paymentAuthorizedProducer;
    }

    @PostMapping("/intent")
    public Mono<PaymentIntentResponseDTO> createPaymentIntent(@Valid @RequestBody PaymentIntentRequestDTO request) {
        return paymentIntentService.create(request);
    }

    @PostMapping("/capture/{intentId}")
    public Mono<ChargeResponseDTO> createCharge(@PathVariable String intentId,
            @Valid @RequestBody ChargeRequestDTO request) {
        return chargeService.create(intentId, request)
                .flatMap(chageConfirm -> {
                    paymentIntentService.updateStatus(intentId, 3)
                            .flatMap(paymentIntent -> {
                                UUID reservationId = UUID.fromString(paymentIntent.reservationId());
                                PaymentAuthorizedEvent event = PaymentAuthorizedEvent.builder()
                                        .paymentIntentId(chageConfirm.paymentIntentId())
                                        .chargeId(chageConfirm.id())
                                        .reservationId(reservationId)
                                        .sagaId(UUID.randomUUID())
                                        .messageId(UUID.randomUUID())
                                        .eventType("PaymentAuthorized.v1")
                                        .build();
                                return Mono.just(paymentAuthorizedProducer.sendMessage(event));
                            }
                            );
                    return Mono.just(chageConfirm);
                });
    }

    @PostMapping("/refund/{chargeId}")
    public Mono<RefundResponseDTO> createRefund(@PathVariable String chargeId,
            @Valid @RequestBody RefundRequestDTO request) {
        return refundServie.create(chargeId, request);
    }

}
