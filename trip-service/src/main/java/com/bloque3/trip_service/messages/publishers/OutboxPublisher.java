package com.bloque3.trip_service.messages.publishers;

import java.time.Duration;
import java.time.Instant;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.bloque3.trip_service.exceptions.MessageQueueException;
import com.bloque3.trip_service.models.outbox.OutboxEvent;
import com.bloque3.trip_service.repositories.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxRepository outboxRepository;
    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    private static final Integer BATCH_SIZE = 50;

    @PostConstruct
    public void start() {
        Flux.interval(Duration.ofMillis(500))
            .flatMap(t -> outboxRepository.findPending(BATCH_SIZE))
            .flatMap(this::publishOutboxEvent, 4)
            .subscribe();
    }

    private Mono<Void> publishOutboxEvent(OutboxEvent e) {
        e.setStatus("SENDING");
        return outboxRepository.save(e)
            .flatMap(ev -> {
                String binding = mapEventToBinding(ev.getEventType());
                try {
                    Object payload = objectMapper.readTree(ev.getPayload());
                    if (payload == null) throw new MessageQueueException("Payload is null");
                    
                    boolean sent = streamBridge.send(binding, 
                        MessageBuilder.withPayload(payload)
                            .setHeader("messageId", e.getMessageId().toString())
                            .setHeader("sagaId", ev.getSagaId())
                            .setHeader("eventType", ev.getEventType())
                            .build()
                    );
                    if (!sent) throw new MessageQueueException("StreamBridge returned false");

                    ev.setStatus("SENT");
                    ev.setProcessedAt(Instant.now());
                    ev.setRetryCount(0);
                    return outboxRepository.save(ev).then();
                    
                } catch (Exception ex) {
                    ev.setStatus("FAILED");
                    ev.setLastError(ex.getMessage());
                    ev.setRetryCount(ev.getRetryCount() == null ? 1: ev.getRetryCount() + 1);
                    return outboxRepository.save(ev).then();   
                }
            });
    }

    private String mapEventToBinding(String eventType) {
        if (eventType.startsWith("ReservationRequested")) return "reservationRequest-out-0";
        if (eventType.startsWith("ReservationConfirmed")) return "reservationConfirmed-out-0";
        if (eventType.startsWith("ReservationCancelled")) return "reservationCancelled-out-0";
        return "reservationRequested-out-0";
    }
}
