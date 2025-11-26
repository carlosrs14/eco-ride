package com.bloque3.payment_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bloque3.payment_service.clients.dtos.ReservationResponseDTO;

@FeignClient(name = "trip-service", path = "/api/v1")
public interface TripClient {

    @GetMapping("/reservations/{id}")
    ReservationResponseDTO getReservationById(@PathVariable String id);

}
