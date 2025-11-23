package com.bloque3.payment_service.exception;

import java.time.Instant;
import java.util.List;

import org.apache.hc.core5.http.HttpStatus;

public record ApiError(

    HttpStatus status,
    String error,
    String message,
    String path,
    Instant timestamp,
    List<String> details
) {}
