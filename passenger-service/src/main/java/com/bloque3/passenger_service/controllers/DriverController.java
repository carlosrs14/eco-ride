package com.bloque3.passenger_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.services.DriverService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("")
    public Mono<DriverResponseDTO> create(@Valid @RequestBody DriverRequestDTO driverRequestDTO) {
        return driverService.create(driverRequestDTO);
    }

    @GetMapping("")
    public Flux<DriverResponseDTO> findAll() {
        return driverService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<DriverResponseDTO> findById(@PathVariable String id) {
        return driverService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<DriverResponseDTO> update(@PathVariable String id,@Valid @RequestBody DriverRequestUpdateDTO driverRequestUpdateDTO) {
        return driverService.update(id,driverRequestUpdateDTO);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return driverService.delete(id);
    }

}
