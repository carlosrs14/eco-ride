package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.ratingDtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.ratingDtos.request.RatingRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.ratingDtos.response.RatingResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {

    Mono<RatingResponseDTO> create(RatingRequestDTO ratingRequestDTO);
    Mono<RatingResponseDTO> findById(String id);
    Flux<RatingResponseDTO> findAll();
    Mono<RatingResponseDTO> update(String id, RatingRequestUpdateDTO ratingRequestUpdateDTO);
    Flux<RatingResponseDTO> findAllByPassengerId(String passengerId);

}
