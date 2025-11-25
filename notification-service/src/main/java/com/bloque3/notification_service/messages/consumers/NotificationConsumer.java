package com.bloque3.notification_service.messages.consumers;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bloque3.notification_service.messages.events.NotificationEvent;
import com.bloque3.notification_service.services.NotificationService;

@Configuration
public class NotificationConsumer {

    private final NotificationService notificationService;

    public NotificationConsumer (NotificationService notificationService){
        this.notificationService = notificationService;
    }
    
    @Bean
    public Consumer<NotificationEvent> notificationRequest(){
        return event ->{
            System.out.println("Recibido");
            notificationService.notify("smtp", event.templateCode(), event.to(), event.params());
        };
    }
}
