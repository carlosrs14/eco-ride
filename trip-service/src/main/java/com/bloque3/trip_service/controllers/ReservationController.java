package com.bloque3.trip_service.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.trip_service.controllers.dto.request.ReservationRequest;
import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;
import com.bloque3.trip_service.services.ReservationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("")
    public Mono<ReservationResponse> create(@Valid @RequestBody ReservationRequest reservationRequest) {
        return reservationService.create(reservationRequest);
    }

    @GetMapping("/{id}")
    public Mono<ReservationResponse> findById(@PathVariable String id) {
        return reservationService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<ReservationResponse> update(@PathVariable String id, @Valid @RequestBody ReservationRequest reservationRequest) {
        return reservationService.update(id, reservationRequest);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return reservationService.delete(id);
    }

    @GetMapping("/trip/{tripId}")
    public Flux<ReservationResponse> findByTripId(@PathVariable String tripId) {
        return reservationService.findByTripId(tripId);
    }

    @GetMapping("/passenger/{passengerId}")
    public Flux<ReservationResponse> findByPassengerId(@PathVariable String passengerId) {
        return reservationService.findByPassengerId(passengerId);
    }

    
}
