package com.bloque3.notification_service.services;

import com.bloque3.notification_service.controllers.request.OutboxRequest;
import com.bloque3.notification_service.controllers.response.OutboxResponse;
import com.bloque3.notification_service.exceptions.ResourceNotFoundException;
import com.bloque3.notification_service.mappers.OutboxMapper;
import com.bloque3.notification_service.models.Outbox;
import com.bloque3.notification_service.repositories.OutboxRepository;
import com.bloque3.notification_service.services.impl.OutboxServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxMapper outboxMapper;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    @Captor
    private ArgumentCaptor<Outbox> outboxCaptor;

    @SuppressWarnings("null")
    @Test
    void create_shouldSaveAndReturnOutbox() {
        String payload = "{\"key\":\"value\"}";
        OutboxRequest request = new OutboxRequest(UUID.randomUUID().toString(), payload, UUID.randomUUID().toString());
        Outbox outbox = Outbox.builder().build();
        Outbox savedOutbox = Outbox.builder().id(UUID.randomUUID()).build();
        OutboxResponse responseDto = new OutboxResponse(savedOutbox.getId().toString(), null, payload, null, 0);

        when(outboxMapper.toEntity(request)).thenReturn(outbox);
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(savedOutbox));
        when(outboxMapper.toDto(savedOutbox)).thenReturn(responseDto);

        StepVerifier.create(outboxService.create(request))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(savedOutbox.getId().toString(), response.id());
                })
                .verifyComplete();

        verify(outboxRepository).save(outboxCaptor.capture());
        Outbox captured = outboxCaptor.getValue();
        assertNotNull(captured.getCreatedAt());
        assertNotNull(captured.getUpdatedAt());
    }

    @SuppressWarnings("null")
    @Test
    void updateStatus_shouldUpdateStatus_whenOutboxExists() {
        UUID outboxId = UUID.randomUUID();
        String newStatusId = UUID.randomUUID().toString();
        Outbox existingOutbox = Outbox.builder().id(outboxId).statusId(UUID.randomUUID().toString()).build();
        Outbox updatedOutbox = Outbox.builder().id(outboxId).statusId(newStatusId).build();
        OutboxResponse responseDto = new OutboxResponse(outboxId.toString(), null, null, newStatusId, 0);
        
        when(outboxRepository.findById(outboxId)).thenReturn(Mono.just(existingOutbox));
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(updatedOutbox));
        when(outboxMapper.toDto(updatedOutbox)).thenReturn(responseDto);

        StepVerifier.create(outboxService.updateStatus(outboxId.toString(), newStatusId))
                .assertNext(response -> {
                    assertEquals(newStatusId, response.statusId());
                })
                .verifyComplete();

        verify(outboxRepository).save(outboxCaptor.capture());
        Outbox captured = outboxCaptor.getValue();
        assertEquals(newStatusId, captured.getStatusId());
        assertNotNull(captured.getUpdatedAt());
    }

    @SuppressWarnings("null")
    @Test
    void updateStatus_shouldThrowException_whenOutboxNotFound() {
        UUID outboxId = UUID.randomUUID();
        String newStatusId = UUID.randomUUID().toString();
        when(outboxRepository.findById(outboxId)).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.updateStatus(outboxId.toString(), newStatusId))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @SuppressWarnings("null")
    @Test
    void incrementRetries_shouldIncrementRetries_whenOutboxExists() {
        UUID outboxId = UUID.randomUUID();
        Outbox existingOutbox = Outbox.builder().id(outboxId).retries(1).build();
        Outbox updatedOutbox = Outbox.builder().id(outboxId).retries(2).build();
        OutboxResponse responseDto = new OutboxResponse(outboxId.toString(), null, null, null, 2);

        when(outboxRepository.findById(outboxId)).thenReturn(Mono.just(existingOutbox));
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(updatedOutbox));
        when(outboxMapper.toDto(updatedOutbox)).thenReturn(responseDto);

        StepVerifier.create(outboxService.incrementRetries(outboxId.toString()))
                .assertNext(response -> {
                    assertEquals(2, response.retries());
                })
                .verifyComplete();

        verify(outboxRepository).save(outboxCaptor.capture());
        Outbox captured = outboxCaptor.getValue();
        assertEquals(2, captured.getRetries());
        assertNotNull(captured.getUpdatedAt());
    }

    @SuppressWarnings("null")
    @Test
    void incrementRetries_shouldThrowException_whenOutboxNotFound() {
        UUID outboxId = UUID.randomUUID();
        when(outboxRepository.findById(outboxId)).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.incrementRetries(outboxId.toString()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateStatus_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";
        String newStatusId = UUID.randomUUID().toString();

        StepVerifier.create(outboxService.updateStatus(invalidId, newStatusId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void incrementRetries_shouldThrowException_whenIdIsInvalid() {
        String invalidId = "invalid-uuid";

        StepVerifier.create(outboxService.incrementRetries(invalidId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
