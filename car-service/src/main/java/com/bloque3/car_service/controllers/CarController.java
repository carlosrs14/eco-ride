package com.bloque3.car_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {
    
    @GetMapping("")
    public String test() {
        return "Hola mundo";
    }
    
}
