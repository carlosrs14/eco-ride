package com.bloque3.trip_service.clients.dtos;

public record DriverResponse(
    String id,
    String passengerId,
    String licenseNo,
    Boolean verificationStatus
){}
