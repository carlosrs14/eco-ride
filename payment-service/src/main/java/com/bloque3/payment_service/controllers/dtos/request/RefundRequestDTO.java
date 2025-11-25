package com.bloque3.payment_service.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefundRequestDTO(

    @NotBlank(message = "amount is required") 
    Double amount

) {}
