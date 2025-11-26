package com.bloque3.payment_service.services;

import com.bloque3.payment_service.controllers.dtos.request.PaymentIntentRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;

import reactor.core.publisher.Mono;

public interface PaymentIntentService {

    Mono<PaymentIntentResponseDTO> create(PaymentIntentRequestDTO paymentIntentRequest);

    Mono<PaymentIntentResponseDTO> updateStatus(String id, Integer statusId);

    Mono<PaymentIntentResponseDTO> findById(String id);
    
}
