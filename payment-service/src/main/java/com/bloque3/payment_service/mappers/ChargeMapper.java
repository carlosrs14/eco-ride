package com.bloque3.payment_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.payment_service.controllers.dtos.request.ChargeRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;
import com.bloque3.payment_service.models.Charge;

@Mapper(componentModel = "spring")
public interface ChargeMapper {
    
    ChargeResponseDTO toDto(Charge charge);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capturedAt", ignore = true)
    @Mapping(target = "paymentIntentId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Charge toEntity(ChargeRequestDTO chargeRequest);
}
