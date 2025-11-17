package com.bloque3.car_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.services.CarService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {
    
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public Mono<CarResponse> create(@Valid @RequestBody CarRequest carRequest) {
        return carService.create(carRequest);
    }

    @GetMapping("/{id}")
    public Mono<CarResponse> findById(@PathVariable String id) {
        return carService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<CarResponse> update(@PathVariable String id, @Valid @RequestBody CarRequest carRequest) {
        return carService.update(id, carRequest);
    }

    @GetMapping("/driver/{driverId}")
    public Flux<CarResponse> findByDriverId(@PathVariable String driverId) {
        return carService.findByDriverId(driverId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return carService.delete(id);
    }
    
}
