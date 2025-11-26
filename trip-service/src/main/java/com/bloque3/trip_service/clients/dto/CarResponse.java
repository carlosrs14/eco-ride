package com.bloque3.trip_service.clients.dto;

public record CarResponse(
    String id,
    String plate,
    String brand,
    String model,
    String color,
    Integer seats,
    String driverId
) {}
