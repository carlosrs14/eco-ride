package com.bloque3.payment_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bloque3.payment_service.controllers.dtos.request.RefundRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.RefundResponseDTO;
import com.bloque3.payment_service.models.Refund;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    RefundResponseDTO toDto(Refund refund);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "chargeId", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Refund toEntity(RefundRequestDTO refundRequestDTO);

}   
