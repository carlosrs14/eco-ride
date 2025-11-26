package com.bloque3.payment_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.payment_service.controllers.dtos.request.PaymentIntentRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.PaymentIntentResponseDTO;
import com.bloque3.payment_service.models.PaymentIntent;

@Mapper(componentModel = "spring")
public interface PaymentIntentMapper {

    PaymentIntentResponseDTO toDto(PaymentIntent paymentIntent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    PaymentIntent toEntity(PaymentIntentRequestDTO createPaymentIntent);

}
