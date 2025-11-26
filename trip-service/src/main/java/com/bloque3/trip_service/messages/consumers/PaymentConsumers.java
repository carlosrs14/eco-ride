package com.bloque3.trip_service.messages.consumers;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.bloque3.trip_service.exceptions.MessageQueueException;
import com.bloque3.trip_service.messages.events.PaymentAuthorizedEvent;
import com.bloque3.trip_service.messages.events.PaymentFailedEvent;
import com.bloque3.trip_service.models.outbox.OutboxEvent;
import com.bloque3.trip_service.models.outbox.ProcessedMessage;
import com.bloque3.trip_service.repositories.OutboxRepository;
import com.bloque3.trip_service.repositories.ProcessedMessageRepository;
import com.bloque3.trip_service.repositories.ReservationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class PaymentConsumers {
    private final ProcessedMessageRepository processedMessageRepository;
    private final OutboxRepository outboxRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionalOperator tx;

    @Bean
    Function<Flux<PaymentAuthorizedEvent>, Flux<Void>> handlePaymentAuthorized() {
        return flux -> flux.flatMap(
            ev -> {
                UUID messageId = ev.messageId();
                return processedMessageRepository.existsByMessageId(messageId)
                    .flatMap(exists -> {
                        if (exists) return Mono.empty();
                        UUID reservationId = ev.reservationId();
                        return tx.execute(
                            status -> 
                                reservationRepository.findActiveByid(reservationId)
                                .flatMap(res -> {
                                    res.setIsActive(true);
                                    return reservationRepository.save(res)
                                        .flatMap(saved -> {
                                            String payload = "{\"reservationId\":\"" + saved.getId() + "\"}";
                                            OutboxEvent outboxEvent = OutboxEvent
                                                .builder()
                                                .aggregateType("Reservation")
                                                .aggregateId(saved.getId())
                                                .eventType("ReservationConfirmed.v1")
                                                .payload(payload)
                                                .messageId(messageId)
                                                .status("PENDING")
                                                .createdAt(Instant.now())
                                                .build();
                                            if (outboxEvent == null) throw new MessageQueueException("Outbox event is null");
                                            return outboxRepository.save(outboxEvent);
                                        });
                                })

                        ).then(
                            processedMessageRepository.save(
                                new ProcessedMessage(
                                    messageId, 
                                    "PaymentAuthorized", 
                                    Instant.now()
                                )
                            )).then();
                    });
            }
        );

    }

    @Bean
    Function<Flux<PaymentFailedEvent>, Flux<Void>> handlePaymentFailed() {
        return flux -> flux.flatMap(
            ev -> {
                UUID messageId = ev.messageId();
                return processedMessageRepository.existsByMessageId(messageId)
                    .flatMap(exists -> {
                        if (exists) return Mono.empty();
                        UUID reservationId = ev.reservationId();
                        return tx.execute(
                            status -> reservationRepository.findActiveByid(reservationId)
                            .flatMap(res -> {
                                res.setIsActive(false);
                                return reservationRepository.save(res)
                                    .flatMap(saved -> {
                                        String payload = "{\"reservationId\":\"" + saved.getId() + "\"}";
                                        OutboxEvent outboxEvent = OutboxEvent
                                            .builder()
                                            .aggregateType("Reservation")
                                            .eventType("ReservationCancelled.v1")
                                            .payload(payload)
                                            .messageId(UUID.randomUUID())
                                            .status("PENDING")
                                            .createdAt(Instant.now())
                                            .build();
                                        if (outboxEvent == null) throw new MessageQueueException("Outbox event is null");
                                        return outboxRepository.save(outboxEvent);
                                    });

                            })
                        ).then(
                            processedMessageRepository.save(
                                new ProcessedMessage(
                                    messageId,
                                    "PaymentFailed",
                                    Instant.now()
                                )
                            )).then();
                    });
            }
        );
    }

}
