package com.bloque3.notification_service.utils.message_handler.impl;

import org.springframework.stereotype.Service;

import com.bloque3.notification_service.utils.message_handler.MessageHandler;

import reactor.core.publisher.Mono;

@Service("sms")
public class SMSMessageHandler implements MessageHandler {

    @Override
    public Mono<Void> send(String subject, String body, String to) {
        System.out.println("SMS implementación");
        return Mono.empty();
    }
    
}
