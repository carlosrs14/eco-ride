package com.bloque3.car_service.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CarRequest(

    @NotBlank(message = "Plate is required") 
    String plate,

    @NotBlank(message = "Brand is required") 
    String brand,
    
    @NotBlank(message = "Model is required") 
    String model,

    @Pattern(
        regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
        message = "Color is not valid"
    )
    String color,
    
    @NotNull(message = "Seats is required")
    @Min(value = 1, message = "Seats must be greater than 0")
    Integer seats,
    
    @NotBlank(message = "Driver id is required")
    String driverId
) {}
