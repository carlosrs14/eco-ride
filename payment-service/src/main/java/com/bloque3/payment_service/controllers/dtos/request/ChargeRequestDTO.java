package com.bloque3.payment_service.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChargeRequestDTO(
    
    @NotBlank(message = "provider id required") 
    String provider,

    @NotBlank(message = "providerRef id required") 
    String providerRef
) {}
