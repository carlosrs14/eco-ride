package com.bloque3.passenger_service.controllers.dtos.request;

import lombok.Builder;

@Builder
public record DriverRequestUpdateDTO(
    String licenseNo,
    boolean verificationStatus
) {}
