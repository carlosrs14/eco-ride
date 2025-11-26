package com.bloque3.trip_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bloque3.trip_service.clients.dto.PassengerResponse;

@FeignClient(name = "passenger-service", path = "/api/v1")
public interface PassengerClient {
    
    @GetMapping("/passengers/{id}")
    PassengerResponse getPassengerById(@PathVariable String id);

}
