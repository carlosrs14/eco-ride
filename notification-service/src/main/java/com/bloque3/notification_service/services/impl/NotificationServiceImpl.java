package com.bloque3.notification_service.services.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bloque3.notification_service.controllers.request.OutboxRequest;
import com.bloque3.notification_service.repositories.EventTypeRepository;
import com.bloque3.notification_service.repositories.StatusRepository;
import com.bloque3.notification_service.services.NotificationService;
import com.bloque3.notification_service.services.OutboxService;
import com.bloque3.notification_service.services.TemplateService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final TemplateService templateService;
    private final OutboxService outboxService;
    private final EventTypeRepository eventTypeRepository;
    private final StatusRepository statusRepository;

    @Override
    public Mono<Void> notify(String templateCode, String to, Map<String, Object> params) {
        return templateService.findByCode(templateCode)
            .flatMap(template -> {
                String body = replaceParams(template.body(), params);
                String subject = replaceParams(template.subject(), params);

                Map<String, Object> payload = Map.of(
                    "to", to,
                    "subject", subject,
                    "body", body
                );

                return eventTypeRepository.findByName("NOTIFICACION")
                    .flatMap(eventType ->
                        statusRepository.findByName("PENDING")
                            .flatMap(status ->
                                outboxService.create(
                                    new OutboxRequest(
                                        eventType.getId().toString(),
                                        payload,
                                        status.getId().toString(),
                                        0 
                                    )
                                )
                            )
                    );
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
