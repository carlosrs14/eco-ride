package com.bloque3.passenger_service.controllers;

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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(PassengerController.class)
public class PassengerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PassengerService passengerService;

    @Test
    void findAll_shouldReturnAllPassengers() {
        PassengerResponseDTO passenger1 = new PassengerResponseDTO(UUID.randomUUID().toString(), "name1", "email1", 4.0f, "sub1");
        PassengerResponseDTO passenger2 = new PassengerResponseDTO(UUID.randomUUID().toString(), "name2", "email2", 4.5f, "sub2");

        when(passengerService.findAll()).thenReturn(Flux.just(passenger1, passenger2));

        webTestClient.get().uri("/api/v1/passengers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PassengerResponseDTO.class)
                .hasSize(2)
                .contains(passenger1, passenger2);
    }

    @Test
    void findAll_shouldReturnEmpty() {
        when(passengerService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/passengers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PassengerResponseDTO.class)
                .hasSize(0);
    }

    @Test
    void findById_shouldReturnPassenger_whenExists() {
        UUID passengerId = UUID.randomUUID();
        PassengerResponseDTO passenger = new PassengerResponseDTO(passengerId.toString(), "name", "email", 4.0f, "sub");

        when(passengerService.findById(passengerId.toString())).thenReturn(Mono.just(passenger));

        webTestClient.get().uri("/api/v1/passengers/{id}", passengerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PassengerResponseDTO.class)
                .isEqualTo(passenger);
    }

    public void findById_shouldReturnBadRequest_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";

        when(passengerService.findById(invalidId)).thenReturn(Mono.error(new IllegalArgumentException()));

        webTestClient.get().uri("/api/v1/passengers/{id}", invalidId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldReturnUpdatedPassenger_whenExistsAndValidRequest() {
        UUID passengerId = UUID.randomUUID();
        PassengerRequestDTO request = new PassengerRequestDTO("user", "pass", "updated@email.com", "Updated Name");
        PassengerResponseDTO updatedPassenger = new PassengerResponseDTO(passengerId.toString(), "Updated Name", "updated@email.com", 4.0f, "sub");

        when(passengerService.update(passengerId.toString(), request)).thenReturn(Mono.just(updatedPassenger));

        webTestClient.put().uri("/api/v1/passengers/{id}", passengerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PassengerResponseDTO.class)
                .isEqualTo(updatedPassenger);
    }

    @Test
    void findByKeycloakSub_shouldReturnPassenger_whenExists() {
        String keycloakSub = UUID.randomUUID().toString();
        PassengerResponseDTO passenger = new PassengerResponseDTO(UUID.randomUUID().toString(), "name", "email", 4.0f, keycloakSub);

        when(passengerService.findByKeycloakSub(keycloakSub)).thenReturn(Mono.just(passenger));

        webTestClient.get().uri("/api/v1/passengers/me/{keycloakSub}", keycloakSub)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PassengerResponseDTO.class)
                .isEqualTo(passenger);
    }

    @Test
    void delete_shouldReturnOk_whenExists() {
        UUID passengerId = UUID.randomUUID();

        when(passengerService.delete(passengerId.toString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/passengers/{id}", passengerId)
                .exchange()
                .expectStatus().isOk();
    }
}
