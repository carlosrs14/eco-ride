package com.bloque3.passenger_service.services;

import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.response.PassengerResponseDTO;
import com.bloque3.passenger_service.exceptions.ResourceNotFoundException;
import com.bloque3.passenger_service.mappers.PassengerMapper;
import com.bloque3.passenger_service.models.Passenger;
import com.bloque3.passenger_service.repositories.PassengerRepository;
import com.bloque3.passenger_service.services.impl.PassengerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Captor
    private ArgumentCaptor<Passenger> passengerCaptor;

    @SuppressWarnings("null")
    @Test
    void create_shouldCreatePassenger() {
        PassengerRequestDTO request = new PassengerRequestDTO("user", "pass", "email@test.com", "Test");
        Passenger passenger = Passenger.builder().build();
        String keycloakSub = UUID.randomUUID().toString();
        Passenger savedPassenger = Passenger.builder().id(UUID.randomUUID()).keycloakSub(keycloakSub).build();
        PassengerResponseDTO responseDto = new PassengerResponseDTO(savedPassenger.getId().toString(), "Test", "email@test.com", 0f, keycloakSub);

        when(passengerMapper.toEntity(request)).thenReturn(passenger);
        when(keycloakService.createuser(request)).thenReturn(Mono.just(keycloakSub));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(Mono.just(savedPassenger));
        when(passengerMapper.toDto(savedPassenger)).thenReturn(responseDto);

        StepVerifier.create(passengerService.create(request))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(keycloakSub, response.keycloakSub());
                })
                .verifyComplete();

        verify(passengerRepository).save(passengerCaptor.capture());
        Passenger captured = passengerCaptor.getValue();
        assertTrue(captured.getIsActive());
        assertEquals(keycloakSub, captured.getKeycloakSub());
    }

    @Test
    void findById_shouldReturnPassenger_whenExists() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).build();
        PassengerResponseDTO responseDto = new PassengerResponseDTO(passengerId.toString(), null, null, null, null);

        when(passengerRepository.findByIdAndIsActiveTrue(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerMapper.toDto(passenger)).thenReturn(responseDto);

        StepVerifier.create(passengerService.findById(passengerId.toString()))
                .assertNext(response -> assertEquals(passengerId.toString(), response.id()))
                .verifyComplete();
    }

    @Test
    void findById_shouldThrowException_whenNotExists() {
        UUID passengerId = UUID.randomUUID();
        when(passengerRepository.findByIdAndIsActiveTrue(passengerId)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.findById(passengerId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void findAll_shouldReturnAllActivePassengers() {
        Passenger passenger1 = Passenger.builder().id(UUID.randomUUID()).build();
        Passenger passenger2 = Passenger.builder().id(UUID.randomUUID()).build();
        PassengerResponseDTO dto1 = new PassengerResponseDTO(passenger1.getId().toString(), null, null, null, null);
        PassengerResponseDTO dto2 = new PassengerResponseDTO(passenger2.getId().toString(), null, null, null, null);

        when(passengerRepository.findAllByIsActiveTrue()).thenReturn(Flux.just(passenger1, passenger2));
        when(passengerMapper.toDto(passenger1)).thenReturn(dto1);
        when(passengerMapper.toDto(passenger2)).thenReturn(dto2);

        StepVerifier.create(passengerService.findAll())
            .expectNextCount(2)
            .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void delete_shouldDeactivatePassenger() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).isActive(true).build();
        
        when(passengerRepository.findByIdAndIsActiveTrue(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));
        
        StepVerifier.create(passengerService.delete(passengerId.toString()))
            .verifyComplete();
            
        verify(passengerRepository).save(passengerCaptor.capture());
        Passenger captured = passengerCaptor.getValue();
        assertFalse(captured.getIsActive());
        assertNotNull(captured.getUpdatedAt());
    }

    @Test
    void findById_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        StepVerifier.create(passengerService.findById(invalidId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void findAll_shouldReturnEmpty_whenNoPassengers() {
        when(passengerRepository.findAllByIsActiveTrue()).thenReturn(Flux.empty());

        StepVerifier.create(passengerService.findAll())
                .verifyComplete();
    }

    @SuppressWarnings("null")
    @Test
    void update_shouldUpdatePassenger_whenExists() {
        UUID passengerId = UUID.randomUUID();
        PassengerRequestDTO request = new PassengerRequestDTO("user", "pass", "newemail@test.com", "New Name");
        Passenger existingPassenger = Passenger.builder().id(passengerId).build();
        Passenger updatedPassenger = Passenger.builder().id(passengerId).name("New Name").build();
        PassengerResponseDTO responseDto = new PassengerResponseDTO(passengerId.toString(), "New Name", null, null, null);

        when(passengerRepository.findByIdAndIsActiveTrue(passengerId)).thenReturn(Mono.just(existingPassenger));
        when(passengerMapper.toEntity(request)).thenReturn(updatedPassenger);
        when(passengerRepository.save(any(Passenger.class))).thenReturn(Mono.just(updatedPassenger));
        when(passengerMapper.toDto(updatedPassenger)).thenReturn(responseDto);

        StepVerifier.create(passengerService.update(passengerId.toString(), request))
                .assertNext(response -> assertEquals("New Name", response.name()))
                .verifyComplete();
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID passengerId = UUID.randomUUID();
        PassengerRequestDTO request = new PassengerRequestDTO("user", "pass", "newemail@test.com", "New Name");
        when(passengerRepository.findByIdAndIsActiveTrue(passengerId)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.update(passengerId.toString(), request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void update_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        PassengerRequestDTO request = new PassengerRequestDTO("user", "pass", "newemail@test.com", "New Name");
        StepVerifier.create(passengerService.update(invalidId, request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID passengerId = UUID.randomUUID();
        when(passengerRepository.findByIdAndIsActiveTrue(passengerId)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.delete(passengerId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void delete_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        StepVerifier.create(passengerService.delete(invalidId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByKeycloakSub_shouldReturnPassenger_whenExists() {
        String keycloakSub = UUID.randomUUID().toString();
        Passenger passenger = Passenger.builder().id(UUID.randomUUID()).keycloakSub(keycloakSub).build();
        PassengerResponseDTO responseDto = new PassengerResponseDTO(passenger.getId().toString(), null, null, null, keycloakSub);

        when(passengerRepository.findByKeycloakSubAndIsActiveTrue(keycloakSub)).thenReturn(Mono.just(passenger));
        when(passengerMapper.toDto(passenger)).thenReturn(responseDto);

        StepVerifier.create(passengerService.findByKeycloakSub(keycloakSub))
                .assertNext(response -> assertEquals(keycloakSub, response.keycloakSub()))
                .verifyComplete();
    }

    @Test
    void findByKeycloakSub_shouldThrowException_whenNotFound() {
        String keycloakSub = UUID.randomUUID().toString();
        when(passengerRepository.findByKeycloakSubAndIsActiveTrue(keycloakSub)).thenReturn(Mono.empty());

        StepVerifier.create(passengerService.findByKeycloakSub(keycloakSub))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
