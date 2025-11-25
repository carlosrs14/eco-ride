package com.bloque3.payment_service.services;

import com.bloque3.payment_service.controllers.dtos.request.ChargeRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;

import reactor.core.publisher.Mono;

public interface ChargeService {

    Mono<ChargeResponseDTO> create(String PaymentIntentId, ChargeRequestDTO chargeRequest);
    Mono<ChargeResponseDTO> findById(String id);
    
}
