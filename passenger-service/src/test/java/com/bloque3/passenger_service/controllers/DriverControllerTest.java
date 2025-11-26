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

import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.services.DriverService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(DriverController.class)
public class DriverControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private DriverService driverService;

    @SuppressWarnings("null")
    @Test
    void create_shouldReturnCreatedDriver_whenValidRequest() {
        UUID passengerId = UUID.randomUUID();
        DriverRequestDTO request = new DriverRequestDTO(passengerId.toString(), "LIC-123");
        DriverResponseDTO response = new DriverResponseDTO(UUID.randomUUID().toString(), passengerId.toString(), "LIC-123", true);

        when(driverService.create(any(DriverRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DriverResponseDTO.class)
                .isEqualTo(response);
    }

    @Test
    void findAll_shouldReturnAllDrivers() {
        DriverResponseDTO driver1 = new DriverResponseDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "LIC-1", true);
        DriverResponseDTO driver2 = new DriverResponseDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "LIC-2", false);

        when(driverService.findAll()).thenReturn(Flux.just(driver1, driver2));

        webTestClient.get().uri("/api/v1/drivers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DriverResponseDTO.class)
                .hasSize(2)
                .contains(driver1, driver2);
    }

    @Test
    void findAll_shouldReturnEmpty() {
        when(driverService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/v1/drivers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DriverResponseDTO.class)
                .hasSize(0);
    }

    @Test
    void findById_shouldReturnDriver_whenExists() {
        UUID driverId = UUID.randomUUID();
        DriverResponseDTO driver = new DriverResponseDTO(driverId.toString(), UUID.randomUUID().toString(), "LIC-123", true);

        when(driverService.findById(driverId.toString())).thenReturn(Mono.just(driver));

        webTestClient.get().uri("/api/v1/drivers/{id}", driverId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DriverResponseDTO.class)
                .isEqualTo(driver);
    }


    @SuppressWarnings("null")
    @Test
    void update_shouldReturnUpdatedDriver_whenExistsAndValidRequest() {
        UUID driverId = UUID.randomUUID();
        DriverRequestUpdateDTO request = DriverRequestUpdateDTO.builder().licenseNo("LIC-456").build();
        DriverResponseDTO updatedDriver = new DriverResponseDTO(driverId.toString(), UUID.randomUUID().toString(), "LIC-456", true);

        when(driverService.update(driverId.toString(), request)).thenReturn(Mono.just(updatedDriver));

        webTestClient.put().uri("/api/v1/drivers/{id}", driverId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DriverResponseDTO.class)
                .isEqualTo(updatedDriver);
    }

    @Test
    void delete_shouldReturnOk_whenExists() {
        UUID driverId = UUID.randomUUID();
        when(driverService.delete(driverId.toString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/drivers/{id}", driverId)
                .exchange()
                .expectStatus().isOk();
    }
}
