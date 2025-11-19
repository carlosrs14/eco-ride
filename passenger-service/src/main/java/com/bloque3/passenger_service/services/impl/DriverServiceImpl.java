package com.bloque3.passenger_service.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.request.DriverRequestUpdateDTO;
import com.bloque3.passenger_service.controllers.dtos.driverDtos.response.DriverResponseDTO;
import com.bloque3.passenger_service.exceptions.ResourceNotFoundException;
import com.bloque3.passenger_service.mappers.DriverMapper;
import com.bloque3.passenger_service.models.Driver;
import com.bloque3.passenger_service.repositories.DriverRepository;
import com.bloque3.passenger_service.services.DriverService;
import com.bloque3.passenger_service.services.PassengerService;

import reactor.core.publisher.Mono;

@Service
public class DriverServiceImpl implements DriverService{

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final PassengerService passengerService;


    public DriverServiceImpl(DriverRepository driverRepository, DriverMapper driverMapper, PassengerService passengerService) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
        this.passengerService = passengerService;
    }
        

    @Override
    public Mono<DriverResponseDTO> create(DriverRequestDTO driverRequestDTO) {
        return passengerService.findById(driverRequestDTO.passengerId())
                .flatMap(passenger -> {
                    Driver driver = driverMapper.toEntity(driverRequestDTO);
                    driver.setIsActive(true);
                    driver.setCreatedAt(Instant.now());
                    driver.setUpdatedAt(Instant.now());
                    return driverRepository.save(driver);
                }).map(driverMapper::toDto);
    }

    @Override
    public Mono<DriverResponseDTO> findById(String id) {
        UUID uuid = UUID.fromString(id);
        return driverRepository.findByIdAndIsActiveTrue(uuid)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("driver", "id", id))
        )
        .map(
            driverMapper::toDto 
        );
    }

    @Override
    public Mono<DriverResponseDTO> update(String id, DriverRequestUpdateDTO driverRequestUpdateDTO) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
        
    }   

    @Override
    public Mono<Void> delete(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    

}
