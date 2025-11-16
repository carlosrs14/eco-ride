package com.bloque3.car_service.services.impl;

import org.springframework.stereotype.Service;

import com.bloque3.car_service.controllers.dto.request.CarRequest;
import com.bloque3.car_service.controllers.dto.response.CarResponse;
import com.bloque3.car_service.services.CarService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CarServiceImpl implements CarService {

    @Override
    public Mono<CarResponse> create(CarRequest carRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public Mono<CarResponse> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Mono<CarResponse> update(String id, CarRequest carRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Flux<CarResponse> findByDriverId(String driverId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByDriverId'");
    }

    @Override
    public Mono<Void> delete(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
}
