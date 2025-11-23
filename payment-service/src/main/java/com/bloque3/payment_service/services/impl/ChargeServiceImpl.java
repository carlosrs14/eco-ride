package com.bloque3.payment_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.payment_service.controllers.dtos.request.ChargeRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.ChargeResponseDTO;
import com.bloque3.payment_service.exception.ResourceNotFoundException;
import com.bloque3.payment_service.mappers.ChargeMapper;
import com.bloque3.payment_service.models.Charge;
import com.bloque3.payment_service.repositories.ChargeRepository;
import com.bloque3.payment_service.repositories.PaymentIntentRepository;
import com.bloque3.payment_service.services.ChargeService;
import com.bloque3.payment_service.services.PaymentIntentService;

import reactor.core.publisher.Mono;

@Service
public class ChargeServiceImpl implements ChargeService {

    private final ChargeRepository chargeRepository;
    private final PaymentIntentService paymentIntentService;
    private final ChargeMapper chargeMapper;

    public ChargeServiceImpl(ChargeRepository chargeRepository, PaymentIntentService paymentIntentService,ChargeMapper chargeMapper) {
        this.chargeRepository = chargeRepository;
        this.paymentIntentService = paymentIntentService;
        this.chargeMapper = chargeMapper;
    }

    @Override
    public Mono<ChargeResponseDTO> create(String paymentIntentId, ChargeRequestDTO chargeRequestDTO) {
        return paymentIntentService.findById(paymentIntentId)
            .flatMap(paymentIntent -> {
            UUID paymentIntentUUID = UUID.fromString(paymentIntentId);
            Charge charge = chargeMapper.toEntity(chargeRequestDTO);
            charge.setPaymentIntentId(paymentIntentUUID);
            charge.setCapturedAt(Instant.now());
            charge.setIsActive(true);
            return chargeRepository.save(charge).map(chargeMapper::toDto);
            });
    }
}
