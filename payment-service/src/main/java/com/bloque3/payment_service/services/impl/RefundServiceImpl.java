package com.bloque3.payment_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.payment_service.controllers.dtos.request.RefundRequestDTO;
import com.bloque3.payment_service.controllers.dtos.response.RefundResponseDTO;
import com.bloque3.payment_service.mappers.RefundMapper;
import com.bloque3.payment_service.models.Refund;
import com.bloque3.payment_service.repositories.RefundRepository;
import com.bloque3.payment_service.services.ChargeService;
import com.bloque3.payment_service.services.RefundService;

import lombok.NonNull;
import reactor.core.publisher.Mono;

@Service
public class RefundServiceImpl implements RefundService {

  private final RefundRepository refundRepository;
  private final ChargeService chargeService;
  private final RefundMapper refundMapper;

  public RefundServiceImpl(RefundRepository refundRepository, ChargeService chargeService,
      RefundMapper refundMapper) {
    this.refundRepository = refundRepository;
    this.chargeService = chargeService;
    this.refundMapper = refundMapper;
  }

  @Override
  public Mono<RefundResponseDTO> create(@NonNull String chargeId, RefundRequestDTO refundRequest) {
    return chargeService.findById(chargeId)
        .flatMap(charge -> {
          UUID chargeUUID = UUID.fromString(chargeId);
          Refund refund = refundMapper.toEntity(refundRequest);
          refund.setChargeId(chargeUUID);
          refund.setCreatedAt(Instant.now());
          return refundRepository.save(refund).map(refundMapper::toDto);
        });
    }
}
