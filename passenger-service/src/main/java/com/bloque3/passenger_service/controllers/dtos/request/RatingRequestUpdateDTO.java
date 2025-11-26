package com.bloque3.passenger_service.controllers.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RatingRequestUpdateDTO(

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Seats must be greater than 0")
    Float score,

    @NotBlank(message = "Comment is required")
    String comment

) {}
