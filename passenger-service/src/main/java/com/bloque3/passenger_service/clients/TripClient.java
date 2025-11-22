package com.bloque3.passenger_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bloque3.passenger_service.clients.dto.TripResponseDTO;


@FeignClient(name = "trip-service",url = "http://trip-service:8082/api/v1")
public interface TripClient {

    @GetMapping("trips/{id}")
    TripResponseDTO getTripById(@PathVariable String id);

}
