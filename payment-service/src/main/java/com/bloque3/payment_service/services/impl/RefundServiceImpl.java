package com.bloque3.payment_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.payment_service.controllers.dtos.request.RefundRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.RefundResponseDTO;
import com.bloque3.payment_service.exception.ResourceNotFoundException;
import com.bloque3.payment_service.mappers.RefundMapper;
import com.bloque3.payment_service.models.Refund;
import com.bloque3.payment_service.repositories.ChargeRepository;
import com.bloque3.payment_service.repositories.RefundRepository;
import com.bloque3.payment_service.services.RefundService;

import reactor.core.publisher.Mono;

@Service
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
  private final ChargeRepository chargeRepository;
  private final RefundMapper refundMapper;

  public RefundServiceImpl(RefundRepository refundRepository, ChargeRepository chargeRepository,
      RefundMapper refundMapper) {
    this.refundRepository = refundRepository;
    this.chargeRepository = chargeRepository;
    this.refundMapper = refundMapper;
  }

  @Override
  public Mono<RefundResponseDTO> create(String chargeId, RefundRequestDTO refundRequest) {
    UUID chargeUUID = UUID.fromString(chargeId);
    return chargeRepository.findById(chargeUUID)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("charge", "id", chargeUUID)))
        .flatMap(charge -> {
          Refund refund = refundMapper.toEntity(refundRequest);
          refund.setChargeId(charge.getId());
          refund.setCreatedAt(Instant.now());
          return refundRepository.save(refund).map(refundMapper::toDto);
        });
    }
}
