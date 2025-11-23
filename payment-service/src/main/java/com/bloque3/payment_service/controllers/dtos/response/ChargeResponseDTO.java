package com.bloque3.payment_service.controllers.dtos.response;

public record ChargeResponseDTO(

    String id, 
    String paymentIntentId, 
    String provider, 
    String providerRef

) {}
