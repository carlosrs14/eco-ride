package com.bloque3.car_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.exception.ResourceNotFoundException;
import com.bloque3.car_service.mappers.CarMapper;
import com.bloque3.car_service.models.Car;
import com.bloque3.car_service.repositories.CarRepository;
import com.bloque3.car_service.services.impl.CarServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Captor
    private ArgumentCaptor<Car> carCaptor;


    @SuppressWarnings("null")
    @Test
    public void testCreateCarSuccess() {
        UUID driverIdUUID = UUID.randomUUID();
        CarRequest request = CarRequest.builder()
            .plate("AAA111").brand("Mazda").model("323").color("#3FDFAF").seats(4).driverId(driverIdUUID.toString()).build();
            
        Car entityToSave = Car.builder().build();

        Car saved = Car.builder()
            .id(UUID.randomUUID())
            .driverId(driverIdUUID)
            .build();

        CarResponse expectedDto = CarResponse.builder().id(saved.getId().toString()).driverId(saved.getDriverId().toString()).build();

        when(carMapper.toEntity(request)).thenReturn(entityToSave);
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(saved));
        when(carMapper.toDto(saved)).thenReturn(expectedDto);

        StepVerifier.create(carService.create(request))
            .assertNext(result -> {
                assertNotNull(result);
                assertEquals(expectedDto.id(), result.id());
            })
            .verifyComplete();

        verify(carMapper, times(1)).toEntity(request);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(carMapper, times(1)).toDto(saved);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    public void testFindById() {
        UUID carId = UUID.randomUUID();
        Car car = Car.builder().id(carId).build();
        CarResponse carResponse = CarResponse.builder().id(carId.toString()).build();

        when(carRepository.findActiveById(carId)).thenReturn(Mono.just(car));
        when(carMapper.toDto(car)).thenReturn(carResponse);

        StepVerifier.create(carService.findById(carId.toString()))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(carId.toString(), response.id());
            })
            .verifyComplete();
        
        verify(carRepository, times(1)).findActiveById(carId);
        verify(carMapper, times(1)).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    public void testFindById_NotFound() {
        UUID carId = UUID.randomUUID();
        when(carRepository.findActiveById(carId)).thenReturn(Mono.empty());

        StepVerifier.create(carService.findById(carId.toString()))
            .expectError(ResourceNotFoundException.class)
            .verify();

        verify(carRepository, times(1)).findActiveById(carId);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdate() {
        UUID carId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        CarRequest request = CarRequest.builder()
            .plate("BBB222").brand("Renault").model("4").color("#FFFFFF").seats(4).driverId(driverId.toString()).build();
        
        Car existingCar = Car.builder().id(carId).build();

        Car updatedCarEntity = Car.builder().id(carId).build();
        
        CarResponse expectedResponse = CarResponse.builder().id(carId.toString()).brand("Renault").build();

        when(carRepository.findActiveById(carId)).thenReturn(Mono.just(existingCar));
        when(carMapper.toEntity(request)).thenReturn(updatedCarEntity);
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(updatedCarEntity));
        when(carMapper.toDto(updatedCarEntity)).thenReturn(expectedResponse);

        StepVerifier.create(carService.update(carId.toString(), request))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals("Renault", response.brand());
            })
            .verifyComplete();

        verify(carRepository, times(1)).findActiveById(carId);
        verify(carRepository, times(1)).save(carCaptor.capture());
        verify(carMapper, times(1)).toEntity(request);
        verify(carMapper, times(1)).toDto(updatedCarEntity);
        assertEquals(carId, carCaptor.getValue().getId());
        assertNotNull(carCaptor.getValue().getUpdatedAt());
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    public void testUpdate_NotFound() {
        UUID carId = UUID.randomUUID();
        CarRequest request = CarRequest.builder().build();

        when(carRepository.findActiveById(carId)).thenReturn(Mono.empty());

        StepVerifier.create(carService.update(carId.toString(), request))
            .expectError(ResourceNotFoundException.class)
            .verify();

        verify(carRepository, times(1)).findActiveById(carId);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete() {
        UUID carId = UUID.randomUUID();
        Car car = Car.builder()
            .id(carId)
            .isActive(true)
            .build();

        when(carRepository.findActiveById(carId)).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.delete(carId.toString()))
            .verifyComplete();

        verify(carRepository, times(1)).findActiveById(carId);
        verify(carRepository, times(1)).save(carCaptor.capture());
        
        Car savedCar = carCaptor.getValue();
        assertEquals(carId, savedCar.getId());
        assertFalse(savedCar.getIsActive());
        assertNotNull(savedCar.getUpdatedAt());
    }

    @Test
    public void testDelete_NotFound() {
        UUID carId = UUID.randomUUID();

        when(carRepository.findActiveById(carId)).thenReturn(Mono.empty());

        StepVerifier.create(carService.delete(carId.toString()))
            .expectError(ResourceNotFoundException.class)
            .verify();

        verify(carRepository, times(1)).findActiveById(carId);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    public void testFindByDriverId() {
        UUID driverId = UUID.randomUUID();
        Car car1 = Car.builder().id(UUID.randomUUID()).driverId(driverId).build();
        Car car2 = Car.builder().id(UUID.randomUUID()).driverId(driverId).build();
        CarResponse response1 = CarResponse.builder().id(car1.getId().toString()).build();
        CarResponse response2 = CarResponse.builder().id(car2.getId().toString()).build();

        when(carRepository.findByActivesByDriverId(driverId)).thenReturn(Flux.just(car1, car2));
        when(carMapper.toDto(car1)).thenReturn(response1);
        when(carMapper.toDto(car2)).thenReturn(response2);

        StepVerifier.create(carService.findByDriverId(driverId.toString()))
            .expectNext(response1)
            .expectNext(response2)
            .verifyComplete();
            
        verify(carRepository, times(1)).findByActivesByDriverId(driverId);
        verify(carMapper, times(2)).toDto(any(Car.class));
        verifyNoMoreInteractions(carRepository, carMapper);
    }
}
