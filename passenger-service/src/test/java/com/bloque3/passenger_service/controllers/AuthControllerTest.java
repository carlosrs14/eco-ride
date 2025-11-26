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

import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.services.PassengerService;

import reactor.core.publisher.Mono;

@WebFluxTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PassengerService passengerService;

    @SuppressWarnings("null")
    @Test
    void register_shouldReturnCreatedPassenger_whenValidRequest() {
        PassengerRequestDTO request = new PassengerRequestDTO("testuser", "testpass", "test@example.com", "Test Name");
        PassengerResponseDTO response = new PassengerResponseDTO(UUID.randomUUID().toString(), "Test Name", "test@example.com", 0f, UUID.randomUUID().toString());

        when(passengerService.create(any(PassengerRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PassengerResponseDTO.class)
                .isEqualTo(response);
    }

    @SuppressWarnings("null")
    @Test
    void register_shouldReturnInternalServerError_whenServiceThrowsError() {
        PassengerRequestDTO request = new PassengerRequestDTO("testuser", "testpass", "test@example.com", "Test Name");

        when(passengerService.create(any(PassengerRequestDTO.class))).thenReturn(Mono.error(new RuntimeException("Service error")));

        webTestClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
