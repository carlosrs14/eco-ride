package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.response.RatingResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {

    Mono<RatingResponseDTO> create(RatingRequestDTO ratingRequestDTO);
    Mono<RatingResponseDTO> findById(String id);
    Flux<RatingResponseDTO> findAll();
    Mono<RatingResponseDTO> update(String id, RatingRequestUpdateDTO ratingRequestUpdateDTO);
    Flux<RatingResponseDTO> findAllByPassengerId(String passengerId);

}
