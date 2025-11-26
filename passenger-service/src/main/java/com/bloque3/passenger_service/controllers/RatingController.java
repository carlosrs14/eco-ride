package com.bloque3.passenger_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.passenger_service.controllers.dtos.response.RatingResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO;
import com.bloque3.passenger_service.services.RatingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("")
    public Mono<RatingResponseDTO> create(@Valid @RequestBody RatingRequestDTO ratingRequestDTO) {
        return ratingService.create(ratingRequestDTO);
    }

    @GetMapping("")
    public Flux<RatingResponseDTO> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<RatingResponseDTO> findAllById(@PathVariable String id) {
        return ratingService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<RatingResponseDTO> update(@PathVariable String id,@Valid @RequestBody RatingRequestUpdateDTO ratingRequestUpdateDTO) {
        return ratingService.update(id,ratingRequestUpdateDTO);
    }

    @GetMapping("/passenger/{id}")
    public Flux<RatingResponseDTO> finAllByPassengerId(@PathVariable String id) {
        return ratingService.findAllByPassengerId(id);
    }
}
