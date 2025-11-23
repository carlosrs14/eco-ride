package com.bloque3.payment_service.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record PaymentIntentRequestDTO(
    
    @NotBlank(message = "reservationId is required") 
    String reservationId,

    @NotBlank(message = "amount is required") 
    Double amount,

    @NotBlank(message = "currency is required") 
    String currency

) {}
