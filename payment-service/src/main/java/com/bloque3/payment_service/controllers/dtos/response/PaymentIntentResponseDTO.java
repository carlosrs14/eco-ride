package com.bloque3.payment_service.controllers.dtos.response;

import lombok.Builder;

@Builder
public record PaymentIntentResponseDTO(
    
    String id,
    String reservationId,
    Double amount,
    String currency,
    String status

) {}
