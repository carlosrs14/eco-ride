package com.bloque3.trip_service.exceptions;

public class MessageQueueException extends ApiException {
    public MessageQueueException(String message) {
        super(message);
    }
}
