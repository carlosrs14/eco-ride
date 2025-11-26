package com.bloque3.passenger_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.services.PassengerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {
    
    private final PassengerService passengerService;

    @PostMapping("")
    public Mono<PassengerResponseDTO> create(@Valid @RequestBody PassengerRequestDTO passengerRequestDTO) {
        return passengerService.create(passengerRequestDTO);
    }

    @GetMapping("")
    public Flux<PassengerResponseDTO> findAll() {
        return passengerService.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<PassengerResponseDTO> finById(@PathVariable String id) {
        return passengerService.findById(id);
    }

    @PutMapping("/{id}")
     public Mono<PassengerResponseDTO> update(@PathVariable String id,@Valid @RequestBody PassengerRequestDTO passengerRequestDTO) {
        return passengerService.update(id,passengerRequestDTO);
    }

    @GetMapping("/me/{keycloakSub}")
    public Mono<PassengerResponseDTO> finByKeycloakSub(@PathVariable String keycloakSub) {
        return passengerService.findByKeycloakSub(keycloakSub);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return passengerService.delete(id);
    }
}
