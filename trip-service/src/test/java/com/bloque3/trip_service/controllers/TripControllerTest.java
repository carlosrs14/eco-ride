package com.bloque3.trip_service.controllers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bloque3.trip_service.controllers.dto.response.TripResponse;
import com.bloque3.trip_service.services.TripService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(TripController.class)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.failFast=false"
})
public class TripControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TripService tripService;

    @Test
    void findById_shouldReturnTrip_whenExists() {
        UUID tripId = UUID.randomUUID();
        TripResponse response = new TripResponse(tripId.toString(), 5, Instant.now().toString(), new BigDecimal("100.00"), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        when(tripService.findById(tripId.toString())).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/trips/{id}", tripId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TripResponse.class)
                .isEqualTo(response);
    }


    @Test
    void delete_shouldReturnOk_whenExists() {
        UUID tripId = UUID.randomUUID();
        when(tripService.delete(tripId.toString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/trips/{id}", tripId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void findByDriverId_shouldReturnTrips_whenExists() {
        UUID driverId = UUID.randomUUID();
        TripResponse trip1 = new TripResponse(UUID.randomUUID().toString(), 5, Instant.now().toString(), new BigDecimal("100.00"), UUID.randomUUID().toString(), UUID.randomUUID().toString(), driverId.toString(), UUID.randomUUID().toString());
        TripResponse trip2 = new TripResponse(UUID.randomUUID().toString(), 4, Instant.now().toString(), new BigDecimal("120.00"), UUID.randomUUID().toString(), UUID.randomUUID().toString(), driverId.toString(), UUID.randomUUID().toString());

        when(tripService.findByDriverId(driverId.toString())).thenReturn(Flux.just(trip1, trip2));

        webTestClient.get().uri("/api/v1/trips/driver/{driverId}", driverId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TripResponse.class)
                .hasSize(2)
                .contains(trip1, trip2);
    }

    @Test
    void findByDriverId_shouldReturnEmpty_whenNoTrips() {
        UUID driverId = UUID.randomUUID();
        when(tripService.findByDriverId(driverId.toString())).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/trips/driver/{driverId}", driverId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TripResponse.class)
                .hasSize(0);
    }

    @Test
    void searchTrips_shouldReturnTrips_whenExists() {
        UUID originId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        TripResponse trip1 = new TripResponse(UUID.randomUUID().toString(), 5, Instant.now().toString(), new BigDecimal("100.00"), originId.toString(), destinationId.toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        TripResponse trip2 = new TripResponse(UUID.randomUUID().toString(), 4, Instant.now().toString(), new BigDecimal("120.00"), originId.toString(), destinationId.toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        when(tripService.searchTrips(originId.toString(), destinationId.toString())).thenReturn(Flux.just(trip1, trip2));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/trips/search")
                .queryParam("originId", originId)
                .queryParam("destinationId", destinationId)
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TripResponse.class)
                .hasSize(2)
                .contains(trip1, trip2);
    }

    @Test
    void searchTrips_shouldReturnEmpty_whenNoTrips() {
        UUID originId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        when(tripService.searchTrips(originId.toString(), destinationId.toString())).thenReturn(Flux.empty());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/trips/search")
                .queryParam("originId", originId)
                .queryParam("destinationId", destinationId)
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TripResponse.class)
                .hasSize(0);
    }

}
