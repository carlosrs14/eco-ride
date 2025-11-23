package com.bloque3.trip_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bloque3.trip_service.clients.dtos.CarResponse;

@FeignClient(name = "car-service", path = "/api/v1")
public interface CarClient {

    @GetMapping("/cars/{id}")
    CarResponse getCarById(@PathVariable String id);
}
