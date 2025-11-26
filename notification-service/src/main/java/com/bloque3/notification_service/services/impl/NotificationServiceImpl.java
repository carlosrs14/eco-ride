package com.bloque3.notification_service.services.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bloque3.notification_service.controllers.request.OutboxRequest;
import com.bloque3.notification_service.repositories.EventTypeRepository;
import com.bloque3.notification_service.repositories.StatusRepository;
import com.bloque3.notification_service.services.NotificationService;
import com.bloque3.notification_service.services.OutboxService;
import com.bloque3.notification_service.services.TemplateService;
import com.bloque3.notification_service.utils.message_handler.MessageHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final TemplateService templateService;
    private final OutboxService outboxService;
    private final EventTypeRepository eventTypeRepository;
    private final StatusRepository statusRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, MessageHandler> handlersMap;

    @Override
    public Mono<Void> notify(String type, String templateCode, String to, Map<String, Object> params) {
        MessageHandler handler = handlersMap.get(type.toLowerCase());
        if (handler == null) {
            return Mono.error(new RuntimeException("Handler not found"));
        }

        return templateService.findByCode(templateCode)
            .flatMap(template -> {
                String body = replaceParams(template.body(), params);
                String subject = replaceParams(template.subject(), params);

                Map<String, Object> payloadMap = Map.of(
                    "to", to,
                    "subject", subject,
                    "body", body
                );

                String payload;
                try {
                    payload = objectMapper.writeValueAsString(payloadMap);
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }

                return eventTypeRepository.findByName("NOTIFICATION")
                    .switchIfEmpty(Mono.error(new RuntimeException("Event type not found")))
                    .flatMap(eventType ->
                        statusRepository.findByName("PENDING")
                            .switchIfEmpty(Mono.error(new RuntimeException("Status not found")))
                            .flatMap(status ->
                                outboxService.create(
                                    new OutboxRequest(
                                        eventType.getId().toString(),
                                        payload,
                                        status.getId().toString()
                                    )
                                )
                            )
                    )
                    .flatMap(outbox -> handler.send(subject, body, to));
            })
            .then();
    }



    private String replaceParams(String text, Map<String, Object> params) {
        String result = text;
        for (var entry : params.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }
        return result;
    }
    
}
