package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.mappers.DriverMapper;
import com.bloque3.passenger_service.models.Driver;
import com.bloque3.passenger_service.repositories.DriverRepository;
import com.bloque3.passenger_service.services.impl.DriverServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private PassengerService passengerService;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Captor
    private ArgumentCaptor<Driver> driverCaptor;

    @SuppressWarnings("null")
    @Test
    void create_shouldCreateDriver_whenPassengerExists() {
        UUID passengerId = UUID.randomUUID();
        DriverRequestDTO request = new DriverRequestDTO(passengerId.toString(), "LIC-123");
        
        PassengerResponseDTO passengerResponse = new PassengerResponseDTO(passengerId.toString(), "name", "email", 0f, "sub");
        Driver driverEntity = Driver.builder().build();
        Driver savedDriver = Driver.builder().id(UUID.randomUUID()).passengerId(passengerId).licenseNo("LIC-123").build();
        DriverResponseDTO driverResponse = new DriverResponseDTO(savedDriver.getId().toString(), passengerId.toString(), "LIC-123", false);

        when(passengerService.findById(passengerId.toString())).thenReturn(Mono.just(passengerResponse));
        when(driverMapper.toEntity(any(DriverRequestDTO.class))).thenReturn(driverEntity);
        when(driverRepository.save(any(Driver.class))).thenReturn(Mono.just(savedDriver));
        when(driverMapper.toDto(savedDriver)).thenReturn(driverResponse);

        StepVerifier.create(driverService.create(request))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertNotNull(response.id());
                    assertTrue(passengerId.toString().equals(response.passengerId()));
                })
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnDriver_whenExists() {
        UUID driverId = UUID.randomUUID();
        Driver driver = Driver.builder().id(driverId).build();
        DriverResponseDTO driverResponse = new DriverResponseDTO(driverId.toString(), null, null, null);

        when(driverRepository.findByIdAndIsActiveTrue(driverId)).thenReturn(Mono.just(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverResponse);
        
        StepVerifier.create(driverService.findById(driverId.toString()))
                .expectNext(driverResponse)
                .verifyComplete();
    }

    @Test
    void create_shouldThrowException_whenPassengerNotFound() {
        UUID passengerId = UUID.randomUUID();
        DriverRequestDTO request = new DriverRequestDTO(passengerId.toString(), "LIC-123");

        when(passengerService.findById(passengerId.toString())).thenReturn(Mono.error(new com.bloque3.passenger_service.exceptions.ResourceNotFoundException("passenger", "id", passengerId.toString())));

        StepVerifier.create(driverService.create(request))
                .expectError(com.bloque3.passenger_service.exceptions.ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        UUID driverId = UUID.randomUUID();
        when(driverRepository.findByIdAndIsActiveTrue(driverId)).thenReturn(Mono.empty());

        StepVerifier.create(driverService.findById(driverId.toString()))
                .expectError(com.bloque3.passenger_service.exceptions.ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findById_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        StepVerifier.create(driverService.findById(invalidId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findAll_shouldReturnAllDrivers() {
        Driver driver1 = Driver.builder().id(UUID.randomUUID()).build();
        Driver driver2 = Driver.builder().id(UUID.randomUUID()).build();
        DriverResponseDTO response1 = new DriverResponseDTO(driver1.getId().toString(), null, null, null);
        DriverResponseDTO response2 = new DriverResponseDTO(driver2.getId().toString(), null, null, null);

        when(driverRepository.findAllByIsActiveTrue()).thenReturn(reactor.core.publisher.Flux.just(driver1, driver2));
        when(driverMapper.toDto(driver1)).thenReturn(response1);
        when(driverMapper.toDto(driver2)).thenReturn(response2);

        StepVerifier.create(driverService.findAll())
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoDrivers() {
        when(driverRepository.findAllByIsActiveTrue()).thenReturn(reactor.core.publisher.Flux.empty());

        StepVerifier.create(driverService.findAll())
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldUpdateDriver_whenExists() {
        UUID driverId = UUID.randomUUID();
        DriverRequestUpdateDTO request = DriverRequestUpdateDTO.builder().licenseNo("NEW-LIC").build();
        Driver existingDriver = Driver.builder().id(driverId).build();
        Driver updatedDriver = Driver.builder().id(driverId).licenseNo("NEW-LIC").build();
        DriverResponseDTO response = new DriverResponseDTO(driverId.toString(), null, "NEW-LIC", null);

        when(driverRepository.findByIdAndIsActiveTrue(driverId)).thenReturn(Mono.just(existingDriver));
        when(driverMapper.toEntity(request)).thenReturn(updatedDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(Mono.just(updatedDriver));
        when(driverMapper.toDto(updatedDriver)).thenReturn(response);

        StepVerifier.create(driverService.update(driverId.toString(), request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID driverId = UUID.randomUUID();
        DriverRequestUpdateDTO request = DriverRequestUpdateDTO.builder().licenseNo("NEW-LIC").build();
        when(driverRepository.findByIdAndIsActiveTrue(driverId)).thenReturn(Mono.empty());

        StepVerifier.create(driverService.update(driverId.toString(), request))
                .expectError(com.bloque3.passenger_service.exceptions.ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void update_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        DriverRequestUpdateDTO request = DriverRequestUpdateDTO.builder().licenseNo("NEW-LIC").build();
        StepVerifier.create(driverService.update(invalidId, request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @SuppressWarnings("null")
    @Test
    void delete_shouldDeactivateDriver_whenExists() {
        UUID driverId = UUID.randomUUID();
        Driver driver = Driver.builder().id(driverId).isActive(true).build();

        when(driverRepository.findByIdAndIsActiveTrue(driverId)).thenReturn(Mono.just(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(Mono.just(driver));

        StepVerifier.create(driverService.delete(driverId.toString()))
                .verifyComplete();
        
        verify(driverRepository).save(driverCaptor.capture());
        Driver captured = driverCaptor.getValue();
        assertFalse(captured.getIsActive());
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID driverId = UUID.randomUUID();
        when(driverRepository.findByIdAndIsActiveTrue(driverId)).thenReturn(Mono.empty());

        StepVerifier.create(driverService.delete(driverId.toString()))
                .expectError(com.bloque3.passenger_service.exceptions.ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void delete_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        StepVerifier.create(driverService.delete(invalidId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}

