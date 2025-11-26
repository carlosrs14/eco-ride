package com.bloque3.notification_service.services;

import com.bloque3.notification_service.controllers.response.OutboxResponse;
import com.bloque3.notification_service.controllers.response.TemplateResponse;
import com.bloque3.notification_service.models.EventType;
import com.bloque3.notification_service.models.Status;
import com.bloque3.notification_service.repositories.EventTypeRepository;
import com.bloque3.notification_service.repositories.StatusRepository;
import com.bloque3.notification_service.services.impl.NotificationServiceImpl;
import com.bloque3.notification_service.utils.message_handler.MessageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private TemplateService templateService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private EventTypeRepository eventTypeRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private Map<String, MessageHandler> handlersMap;
    @Mock
    private MessageHandler messageHandler;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        // We need to re-inject the map because of the way @InjectMocks works with collections
        notificationService = new NotificationServiceImpl(
            templateService, outboxService, eventTypeRepository, statusRepository, objectMapper, handlersMap
        );
    }

    @Test
    void notify_shouldProcessAndSendNotification() throws Exception {
        String type = "smtp";
        String templateCode = "WELCOME";
        String to = "test@example.com";
        Map<String, Object> params = Map.of("name", "Test User");

        TemplateResponse templateResponse = new TemplateResponse(
                UUID.randomUUID().toString(),
                templateCode,
                UUID.randomUUID().toString(),
                "Welcome {{name}}",
                "Hello {{name}}, welcome to our service!"
        );

        EventType eventType = EventType.builder().id(UUID.randomUUID()).name("NOTIFICATION").build();
        Status status = Status.builder().id(UUID.randomUUID()).name("PENDING").build();
        String payload = "{\"to\":\"test@example.com\",\"subject\":\"Welcome Test User\",\"body\":\"Hello Test User, welcome to our service!\"}";
        OutboxResponse outboxResponse = new OutboxResponse(UUID.randomUUID().toString(), eventType.getId().toString(), payload, status.getId().toString(), 0);

        when(handlersMap.get(type)).thenReturn(messageHandler);
        when(templateService.findByCode(templateCode)).thenReturn(Mono.just(templateResponse));
        when(objectMapper.writeValueAsString(any())).thenReturn(payload);
        when(eventTypeRepository.findByName("NOTIFICATION")).thenReturn(Mono.just(eventType));
        when(statusRepository.findByName("PENDING")).thenReturn(Mono.just(status));
        when(outboxService.create(any())).thenReturn(Mono.just(outboxResponse));
        when(messageHandler.send(any(String.class), any(String.class), any(String.class))).thenReturn(Mono.empty());

        StepVerifier.create(notificationService.notify(type, templateCode, to, params))
                .verifyComplete();

        verify(templateService).findByCode(templateCode);
        verify(eventTypeRepository).findByName("NOTIFICATION");
        verify(statusRepository).findByName("PENDING");
        verify(outboxService).create(any());
        verify(messageHandler).send("Welcome Test User", "Hello Test User, welcome to our service!", to);
    }

    @Test
    void notify_shouldReturnError_whenTemplateNotFound() {
        String type = "smtp";
        String templateCode = "NON_EXISTENT";
        String to = "test@example.com";
        Map<String, Object> params = Map.of("name", "Test User");

        when(handlersMap.get(type)).thenReturn(messageHandler);
        when(templateService.findByCode(templateCode)).thenReturn(Mono.error(new RuntimeException("Template not found")));

        StepVerifier.create(notificationService.notify(type, templateCode, to, params))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Template not found"))
                .verify();
    }

    @Test
    void notify_shouldReturnError_whenEventTypeNotFound() throws Exception {
        String type = "smtp";
        String templateCode = "WELCOME";
        String to = "test@example.com";
        Map<String, Object> params = Map.of("name", "Test User");

        TemplateResponse templateResponse = new TemplateResponse(
                UUID.randomUUID().toString(),
                templateCode,
                UUID.randomUUID().toString(),
                "Welcome {{name}}",
                "Hello {{name}}, welcome to our service!"
        );

        String payload = "{\"to\":\"test@example.com\",\"subject\":\"Welcome Test User\",\"body\":\"Hello Test User, welcome to our service!\"}";

        when(handlersMap.get(type)).thenReturn(messageHandler);
        when(templateService.findByCode(templateCode)).thenReturn(Mono.just(templateResponse));
        when(objectMapper.writeValueAsString(any())).thenReturn(payload);
        when(eventTypeRepository.findByName("NOTIFICATION")).thenReturn(Mono.empty());

        StepVerifier.create(notificationService.notify(type, templateCode, to, params))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Event type not found"))
                .verify();
    }

    @Test
    void notify_shouldReturnError_whenStatusNotFound() throws Exception {
        String type = "smtp";
        String templateCode = "WELCOME";
        String to = "test@example.com";
        Map<String, Object> params = Map.of("name", "Test User");

        TemplateResponse templateResponse = new TemplateResponse(
                UUID.randomUUID().toString(),
                templateCode,
                UUID.randomUUID().toString(),
                "Welcome {{name}}",
                "Hello {{name}}, welcome to our service!"
        );

        EventType eventType = EventType.builder().id(UUID.randomUUID()).name("NOTIFICATION").build();
        String payload = "{\"to\":\"test@example.com\",\"subject\":\"Welcome Test User\",\"body\":\"Hello Test User, welcome to our service!\"}";

        when(handlersMap.get(type)).thenReturn(messageHandler);
        when(templateService.findByCode(templateCode)).thenReturn(Mono.just(templateResponse));
        when(objectMapper.writeValueAsString(any())).thenReturn(payload);
        when(eventTypeRepository.findByName("NOTIFICATION")).thenReturn(Mono.just(eventType));
        when(statusRepository.findByName("PENDING")).thenReturn(Mono.empty());

        StepVerifier.create(notificationService.notify(type, templateCode, to, params))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Status not found"))
                .verify();
    }

    @Test
    void notify_shouldReturnError_whenJsonProcessingException() throws Exception {
        String type = "smtp";
        String templateCode = "WELCOME";
        String to = "test@example.com";
        Map<String, Object> params = Map.of("name", "Test User");

        TemplateResponse templateResponse = new TemplateResponse(
                UUID.randomUUID().toString(),
                templateCode,
                UUID.randomUUID().toString(),
                "Welcome {{name}}",
                "Hello {{name}}, welcome to our service!"
        );

        when(handlersMap.get(type)).thenReturn(messageHandler);
        when(templateService.findByCode(templateCode)).thenReturn(Mono.just(templateResponse));
        when(objectMapper.writeValueAsString(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Error") {});

        StepVerifier.create(notificationService.notify(type, templateCode, to, params))
                .expectError(com.fasterxml.jackson.core.JsonProcessingException.class)
                .verify();
    }

    @Test
    void notify_shouldReturnError_whenHandlerNotFound() {
        String type = "non-existent-handler";
        String templateCode = "WELCOME";
        String to = "test@example.com";
        Map<String, Object> params = Map.of("name", "Test User");

        when(handlersMap.get(type)).thenReturn(null);

        StepVerifier.create(notificationService.notify(type, templateCode, to, params))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Handler not found"))
                .verify();
    }
}
