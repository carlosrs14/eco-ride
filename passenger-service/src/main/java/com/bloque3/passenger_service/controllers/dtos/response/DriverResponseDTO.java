package com.bloque3.passenger_service.controllers.dtos.response;

public record DriverResponseDTO(
    String id,
    String passengerId,
    String licenseNo,
    Boolean verificationStatus
) {}
