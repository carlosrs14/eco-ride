package com.bloque3.car_service.controllers.dto.request;

public record CarRequest(
    String plate,
    String brand,
    String model,
    String color,
    Integer sits,
    String driverId
) {}
