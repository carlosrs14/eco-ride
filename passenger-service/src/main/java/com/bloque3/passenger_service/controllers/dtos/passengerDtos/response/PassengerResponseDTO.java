package com.bloque3.passenger_service.controllers.dtos.passengerDtos.response;

public record PassengerResponseDTO(

    String id,
    String name,
    String email,
    Float ratingAvg,
    String keycloakSub
){}



