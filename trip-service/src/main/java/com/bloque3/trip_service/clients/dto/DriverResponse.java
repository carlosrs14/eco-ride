package com.bloque3.trip_service.clients.dto;

public record DriverResponse(
    String id,
    String passengerId,
    String licenseNo,
    Boolean verificationStatus
){}
