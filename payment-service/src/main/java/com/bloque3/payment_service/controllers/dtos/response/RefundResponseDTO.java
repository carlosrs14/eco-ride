package com.bloque3.payment_service.controllers.dtos.response;

import lombok.Builder;

@Builder
public record RefundResponseDTO(

    String id, 
    String chargeId, 
    Double amount, 
    String reason

) {}
