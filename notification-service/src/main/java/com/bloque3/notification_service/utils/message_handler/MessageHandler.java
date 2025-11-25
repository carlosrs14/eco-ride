package com.bloque3.notification_service.utils.message_handler;

import reactor.core.publisher.Mono;

public interface MessageHandler {
    Mono<Void> send(String subject, String body, String to);
    
}
