package com.bloque3.car_service.controllers.dto.response;

import lombok.Builder;

@Builder
public record CarResponse(
    String id,
    String plate,
    String brand,
    String model,
    String color,
    Integer seats,
    String driverId
) {}
