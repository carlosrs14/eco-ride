package com.bloque3.trip_service.controllers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bloque3.trip_service.controllers.dto.response.ReservationResponse;
import com.bloque3.trip_service.services.ReservationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReservationService reservationService;


    @Test
    void findById_shouldReturnReservation_whenExists() {
        UUID reservationId = UUID.randomUUID();
        ReservationResponse response = new ReservationResponse(reservationId.toString(), UUID.randomUUID().toString(), new BigDecimal("100.00"), UUID.randomUUID().toString(), 2);

        when(reservationService.findById(reservationId.toString())).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/reservations/{id}", reservationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationResponse.class)
                .isEqualTo(response);
    }

    @Test
    void delete_shouldReturnOk_whenExists() {
        UUID reservationId = UUID.randomUUID();
        when(reservationService.delete(reservationId.toString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/reservations/{id}", reservationId)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void findByTripId_shouldReturnReservations_whenExists() {
        UUID tripId = UUID.randomUUID();
        ReservationResponse res1 = new ReservationResponse(UUID.randomUUID().toString(), tripId.toString(), new BigDecimal("100.00"), UUID.randomUUID().toString(), 2);
        ReservationResponse res2 = new ReservationResponse(UUID.randomUUID().toString(), tripId.toString(), new BigDecimal("150.00"), UUID.randomUUID().toString(), 3);

        when(reservationService.findByTripId(tripId.toString())).thenReturn(Flux.just(res1, res2));

        webTestClient.get().uri("/api/v1/reservations/trip/{tripId}", tripId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReservationResponse.class)
                .hasSize(2)
                .contains(res1, res2);
    }

    @Test
    void findByTripId_shouldReturnEmpty_whenNoReservations() {
        UUID tripId = UUID.randomUUID();
        when(reservationService.findByTripId(tripId.toString())).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/reservations/trip/{tripId}", tripId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReservationResponse.class)
                .hasSize(0);
    }

    @Test
    void findByPassengerId_shouldReturnReservations_whenExists() {
        UUID passengerId = UUID.randomUUID();
        ReservationResponse res1 = new ReservationResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new BigDecimal("100.00"), passengerId.toString(), 2);
        ReservationResponse res2 = new ReservationResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new BigDecimal("150.00"), passengerId.toString(), 3);

        when(reservationService.findByPassengerId(passengerId.toString())).thenReturn(Flux.just(res1, res2));

        webTestClient.get().uri("/api/v1/reservations/passenger/{passengerId}", passengerId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReservationResponse.class)
                .hasSize(2)
                .contains(res1, res2);
    }

    @Test
    void findByPassengerId_shouldReturnEmpty_whenNoReservations() {
        UUID passengerId = UUID.randomUUID();
        when(reservationService.findByPassengerId(passengerId.toString())).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/reservations/passenger/{passengerId}", passengerId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReservationResponse.class)
                .hasSize(0);
    }
}
