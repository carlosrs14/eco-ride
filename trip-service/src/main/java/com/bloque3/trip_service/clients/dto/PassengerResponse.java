package com.bloque3.trip_service.clients.dto;

public record PassengerResponse(
    String id,
    String name,
    String email,
    Float ratingAvg,
    String keycloakSub

) {}
