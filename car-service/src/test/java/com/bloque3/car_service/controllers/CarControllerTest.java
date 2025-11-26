package com.bloque3.car_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.services.CarService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CarService carService;

    @SuppressWarnings("null")
    @Test
    public void testCreateCar_Success() {
        UUID driverId = UUID.randomUUID();
        CarRequest request = CarRequest.builder()
                .plate("AAA111")
                .brand("Mazda")
                .model("323")
                .color("#3FDFAF")
                .seats(4)
                .driverId(driverId.toString())
                .build();

        CarResponse response = CarResponse.builder()
                .id(UUID.randomUUID().toString())
                .plate("AAA111")
                .brand("Mazda")
                .model("323")
                .color("#3FDFAF")
                .seats(4)
                .driverId(driverId.toString())
                .build();

        when(carService.create(any(CarRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CarResponse.class)
                .isEqualTo(response);
    }

    @SuppressWarnings("null")
	@Test
    public void testFindById_Success() {
        UUID carId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        CarResponse response = CarResponse.builder()
                .id(carId.toString())
                .plate("AAA111")
                .brand("Mazda")
                .model("323")
                .color("#3FDFAF")
                .seats(4)
                .driverId(driverId.toString())
                .build();

        when(carService.findById(carId.toString())).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/cars/{id}", carId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CarResponse.class)
                .isEqualTo(response);
    }

    @SuppressWarnings("null")
	@Test
    public void testUpdate_Success() {
        UUID carId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        CarRequest request = CarRequest.builder()
                .plate("AAA111")
                .brand("Mazda")
                .model("323")
                .color("#3FDFAF")
                .seats(4)
                .driverId(driverId.toString())
                .build();

        CarResponse response = CarResponse.builder()
                .id(carId.toString())
                .plate("AAA111")
                .brand("Mazda")
                .model("323")
                .color("#3FDFAF")
                .seats(4)
                .driverId(driverId.toString())
                .build();

        when(carService.update(carId.toString(), request)).thenReturn(Mono.just(response));

        webTestClient.put().uri("/api/v1/cars/{id}", carId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CarResponse.class)
                .isEqualTo(response);
    }

    @Test
    public void testFindByDriverId_Success() {
        UUID driverId = UUID.randomUUID();
        when(carService.findByDriverId(driverId.toString())).thenReturn(Flux.just(
            CarResponse.builder().build(),
            CarResponse.builder().build()
        ));

        webTestClient.get().uri("/api/v1/cars/driver/{driverId}", driverId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CarResponse.class)
                .hasSize(2);
    }


    @Test
    public void testDelete_Success() {
        UUID carId = UUID.randomUUID();
        when(carService.delete(carId.toString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/cars/{id}", carId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testDelete_NotFound() {
        UUID carId = UUID.randomUUID();
        when(carService.delete(carId.toString())).thenReturn(Mono.error(new RuntimeException()));

        webTestClient.delete().uri("/api/v1/cars/{id}", carId)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}
