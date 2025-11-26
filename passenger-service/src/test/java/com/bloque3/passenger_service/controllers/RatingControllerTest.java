package com.bloque3.passenger_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.RatingRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.response.RatingResponseDTO;
import com.bloque3.passenger_service.services.RatingService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(RatingController.class)
public class RatingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RatingService ratingService;

    @SuppressWarnings("null")
    @Test
    void create_shouldReturnCreatedRating_whenValidRequest() {
        UUID tripId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        RatingRequestDTO request = new RatingRequestDTO(tripId.toString(), fromId.toString(), toId.toString(), 5f, "Excellent");
        RatingResponseDTO response = new RatingResponseDTO(UUID.randomUUID().toString(), tripId.toString(), fromId.toString(), toId.toString(), 5f, "Excellent");

        when(ratingService.create(any(RatingRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatingResponseDTO.class)
                .isEqualTo(response);
    }

    @Test
    void findAll_shouldReturnAllRatings() {
        RatingResponseDTO rating1 = new RatingResponseDTO(UUID.randomUUID().toString(), null, null, null, 4.0f, "Good");
        RatingResponseDTO rating2 = new RatingResponseDTO(UUID.randomUUID().toString(), null, null, null, 4.5f, "Great");

        when(ratingService.findAll()).thenReturn(Flux.just(rating1, rating2));

        webTestClient.get().uri("/api/v1/ratings")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RatingResponseDTO.class)
                .hasSize(2)
                .contains(rating1, rating2);
    }

    @Test
    void findAll_shouldReturnEmpty() {
        when(ratingService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/ratings")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RatingResponseDTO.class)
                .hasSize(0);
    }

    @Test
    void findById_shouldReturnRating_whenExists() {
        UUID ratingId = UUID.randomUUID();
        RatingResponseDTO rating = new RatingResponseDTO(ratingId.toString(), null, null, null, 4.0f, "Good");

        when(ratingService.findById(ratingId.toString())).thenReturn(Mono.just(rating));

        webTestClient.get().uri("/api/v1/ratings/{id}", ratingId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatingResponseDTO.class)
                .isEqualTo(rating);
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldReturnUpdatedRating_whenExistsAndValidRequest() {
        UUID ratingId = UUID.randomUUID();
        RatingRequestUpdateDTO request = new RatingRequestUpdateDTO(4.5f, "Updated Comment");
        RatingResponseDTO updatedRating = new RatingResponseDTO(ratingId.toString(), null, null, null, 4.5f, "Updated Comment");

        when(ratingService.update(ratingId.toString(), request)).thenReturn(Mono.just(updatedRating));

        webTestClient.put().uri("/api/v1/ratings/{id}", ratingId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatingResponseDTO.class)
                .isEqualTo(updatedRating);
    }
    @Test
    void findByPassengerId_shouldReturnRatings_whenExists() {
        UUID passengerId = UUID.randomUUID();
        RatingResponseDTO rating1 = new RatingResponseDTO(UUID.randomUUID().toString(), null, null, passengerId.toString(), 4.0f, "Good");
        RatingResponseDTO rating2 = new RatingResponseDTO(UUID.randomUUID().toString(), null, null, passengerId.toString(), 4.5f, "Great");

        when(ratingService.findAllByPassengerId(passengerId.toString())).thenReturn(Flux.just(rating1, rating2));

        webTestClient.get().uri("/api/v1/ratings/passenger/{id}", passengerId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RatingResponseDTO.class)
                .hasSize(2)
                .contains(rating1, rating2);
    }

    @Test
    void findByPassengerId_shouldReturnEmpty_whenNotExists() {
        UUID passengerId = UUID.randomUUID();
        when(ratingService.findAllByPassengerId(passengerId.toString())).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/ratings/passenger/{id}", passengerId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RatingResponseDTO.class)
                .hasSize(0);
    }
}
