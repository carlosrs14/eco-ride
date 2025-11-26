package com.bloque3.passenger_service.controllers.dtos.request;

public record DriverRequestUpdateDTO(

    String licenseNo,

    boolean verificationStatus
    
) {}
