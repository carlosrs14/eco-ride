package com.bloque3.passenger_service.controllers.dtos.ratingDtos.response;


public record RatingResponseDTO(

    String id,
    String tripId,
    String fromId,
    String toId,
    Float score,
    String comment
) {} 

