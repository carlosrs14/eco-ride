package com.bloque3.car_service.controllers.dto.response;

public record CarResponse(
    String id,
    String plate,
    String brand,
    String model,
    String color,
    Integer sits,
    String driverId
) {}
