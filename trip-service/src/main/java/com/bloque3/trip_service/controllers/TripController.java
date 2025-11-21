package com.bloque3.trip_service.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.trip_service.controllers.dto.request.TripRequest;
import com.bloque3.trip_service.controllers.dto.response.TripResponse;
import com.bloque3.trip_service.services.TripService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("")
    public Mono<TripResponse> create(@Valid @RequestBody TripRequest tripRequest) {
        return tripService.create(tripRequest);
    }

    @GetMapping("/{id}")
    public Mono<TripResponse> findById(@PathVariable String id) {
        return tripService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<TripResponse> update(@PathVariable String id, @Valid @RequestBody TripRequest tripRequest) {
        return tripService.update(id, tripRequest);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return tripService.delete(id);
    }

    @GetMapping("/driver/{driverId}")
    public Flux<TripResponse> findByDriverId(@PathVariable String driverId) {
        return tripService.findByDriverId(driverId);
    }

    @GetMapping("/search")
    public Flux<TripResponse> searchTrips(@RequestParam String originId, @RequestParam String destinationId) {
        return tripService.searchTrips(originId, destinationId);
    }

    
}
