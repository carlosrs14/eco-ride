package com.bloque3.trip_service.clients.dtos;

public record PassengerResponse(
    String id,
    String name,
    String email,
    Float ratingAvg,
    String keycloakSub

) {}
