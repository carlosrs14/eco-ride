package com.bloque3.trip_service.controllers.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Location type id is required")
    String locationTypeId

){}
