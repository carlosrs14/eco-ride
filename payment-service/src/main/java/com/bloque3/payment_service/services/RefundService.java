package com.bloque3.payment_service.services;

import com.bloque3.payment_service.controllers.dtos.request.RefundRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.RefundResponseDTO;

import reactor.core.publisher.Mono;

public interface RefundService {

    Mono<RefundResponseDTO> create(String chargeId, RefundRequestDTO refundRequest);

}
