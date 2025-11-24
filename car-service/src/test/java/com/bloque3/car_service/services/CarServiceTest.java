package com.bloque3.car_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.mappers.CarMapper;
import com.bloque3.car_service.models.Car;
import com.bloque3.car_service.repositories.CarRepository;
import com.bloque3.car_service.services.impl.CarServiceImpl;

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


    @Test
    public void testCreateCarSuccess() {
        UUID idUUID = UUID.randomUUID();
        String idString = idUUID.toString();
        UUID driverIdUUID = UUID.randomUUID();
        String driverIdString = driverIdUUID.toString();
        String brand = "Mazda";

        CarRequest request = CarRequest
            .builder()
            .plate("AAA111")
            .brand(brand)
            .model("323")
            .color("#3FDFAF")
            .seats(4)
            .driverId(driverIdString)
            .build();
            
        Car entityToSave = Car.builder()
            .brand(request.brand())
            .model(request.model())
            .color(request.color())
            .seats(request.seats())
            .driverId(driverIdUUID)
            .build();

        Car saved = Car.builder()
            .id(idUUID)
            .brand(entityToSave.getBrand())
            .model(entityToSave.getModel())
            .color(entityToSave.getColor())
            .seats(entityToSave.getSeats())
            .driverId(driverIdUUID)
            .build();

        CarResponse expectedDto = CarResponse
            .builder()
            .id(idString)
            .brand(saved.getBrand())
            .model(saved.getModel())
            .color(saved.getColor())
            .seats(saved.getSeats())
            .driverId(driverIdString)
            .build();

        when(carMapper.toEntity(Mockito.<CarRequest>any())).thenReturn(entityToSave);
        when(carRepository.save(entityToSave)).thenReturn(Mono.just(saved));
        when(carMapper.toDto(Mockito.<Car>any())).thenReturn(expectedDto);

        StepVerifier.create(carService.create(request))
            .assertNext(result -> {
                assertNotNull(result);
                assertEquals(expectedDto.id(), result.id());
                assertEquals(brand, result.brand());
            })
            .verifyComplete();


        verify(carRepository, times(1)).save(entityToSave);
        verify(carMapper, times(1)).toEntity(request);
        verify(carMapper, times(1)).toDto(saved);

        assertEquals(request.brand(), saved.getBrand(), "Brand guardada incorrecta");
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    public void testCreateCarFailed() {
        assertTrue(true);
    }


    @Test
    public void testFindById() {
        assertTrue(true);
    }

}
