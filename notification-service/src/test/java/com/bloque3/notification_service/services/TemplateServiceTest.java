package com.bloque3.notification_service.services;

import com.bloque3.notification_service.controllers.request.TemplateRequest;
import com.bloque3.notification_service.controllers.response.TemplateResponse;
import com.bloque3.notification_service.exceptions.ResourceNotFoundException;
import com.bloque3.notification_service.mappers.TemplateMapper;
import com.bloque3.notification_service.models.Template;
import com.bloque3.notification_service.repositories.TemplateRepository;
import com.bloque3.notification_service.services.impl.TemplateServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateMapper templateMapper;

    @InjectMocks
    private TemplateServiceImpl templateService;

    @Captor
    private ArgumentCaptor<Template> templateCaptor;

    @SuppressWarnings("null")
    @Test
    public void testCreateTemplate() {
        UUID channelId = UUID.randomUUID();
        TemplateRequest request = new TemplateRequest("CODE-01", channelId.toString(), "Subject", "Body");
        Template entity = Template.builder().build();
        Template savedEntity = Template.builder().id(UUID.randomUUID()).build();
        TemplateResponse responseDto = new TemplateResponse(savedEntity.getId().toString(), "CODE-01", channelId.toString(), "Subject", "Body");

        when(templateMapper.toEntity(request)).thenReturn(entity);
        when(templateRepository.save(any(Template.class))).thenReturn(Mono.just(savedEntity));
        when(templateMapper.toDto(savedEntity)).thenReturn(responseDto);

        StepVerifier.create(templateService.create(request))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(savedEntity.getId().toString(), response.id());
            })
            .verifyComplete();

        verify(templateRepository).save(templateCaptor.capture());
        Template captured = templateCaptor.getValue();
        assertTrue(captured.getIsActive());
        assertNotNull(captured.getCreatedAt());
        assertNotNull(captured.getUpdatedAt());
    }

    @Test
    public void testFindById_Success() {
        UUID templateId = UUID.randomUUID();
        Template template = Template.builder().id(templateId).build();
        TemplateResponse responseDto = new TemplateResponse(templateId.toString(), "CODE", null, null, null);

        when(templateRepository.findActiveById(templateId)).thenReturn(Mono.just(template));
        when(templateMapper.toDto(template)).thenReturn(responseDto);

        StepVerifier.create(templateService.findById(templateId.toString()))
            .assertNext(response -> {
                assertEquals(templateId.toString(), response.id());
            })
            .verifyComplete();
    }

    @Test
    public void testFindById_NotFound() {
        UUID templateId = UUID.randomUUID();
        when(templateRepository.findActiveById(templateId)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.findById(templateId.toString()))
            .expectError(ResourceNotFoundException.class)
            .verify();
    }

    @Test
    public void testFindByCode_Success() {
        String code = "WELCOME_EMAIL";
        Template template = Template.builder().id(UUID.randomUUID()).code(code).build();
        TemplateResponse responseDto = new TemplateResponse(template.getId().toString(), code, null, null, null);

        when(templateRepository.findByCode(code)).thenReturn(Mono.just(template));
        when(templateMapper.toDto(template)).thenReturn(responseDto);

        StepVerifier.create(templateService.findByCode(code))
            .assertNext(response -> {
                assertEquals(code, response.code());
            })
            .verifyComplete();
    }

    @Test
    public void testFindByCode_NotFound() {
        String code = "NON_EXISTENT";
        when(templateRepository.findByCode(code)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.findByCode(code))
            .expectError(ResourceNotFoundException.class)
            .verify();
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdate_Success() {
        UUID templateId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        TemplateRequest request = new TemplateRequest("UPDATED-CODE", channelId.toString(), "Updated Subject", "Updated Body");
        Template existingTemplate = Template.builder().id(templateId).isActive(true).build();
        Template updatedEntity = Template.builder().id(templateId).build();
        TemplateResponse responseDto = new TemplateResponse(templateId.toString(), "UPDATED-CODE", channelId.toString(), "Updated Subject", "Updated Body");

        when(templateRepository.findActiveById(templateId)).thenReturn(Mono.just(existingTemplate));
        when(templateMapper.toEntity(request)).thenReturn(updatedEntity);
        when(templateRepository.save(any(Template.class))).thenReturn(Mono.just(updatedEntity));
        when(templateMapper.toDto(updatedEntity)).thenReturn(responseDto);
        
        StepVerifier.create(templateService.update(templateId.toString(), request))
            .assertNext(response -> {
                assertEquals("UPDATED-CODE", response.code());
            })
            .verifyComplete();
        
        verify(templateRepository).save(templateCaptor.capture());
        Template captured = templateCaptor.getValue();
        assertEquals(templateId, captured.getId());
        assertNotNull(captured.getUpdatedAt());
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete_Success() {
        UUID templateId = UUID.randomUUID();
        Template template = Template.builder().id(templateId).isActive(true).build();

        when(templateRepository.findActiveById(templateId)).thenReturn(Mono.just(template));
        when(templateRepository.save(any(Template.class))).thenReturn(Mono.just(template));

        StepVerifier.create(templateService.delete(templateId.toString()))
            .verifyComplete();

        verify(templateRepository).save(templateCaptor.capture());
        Template captured = templateCaptor.getValue();
        assertFalse(captured.getIsActive());
        assertNotNull(captured.getUpdatedAt());
    }

    @Test
    public void testUpdate_NotFound() {
        UUID templateId = UUID.randomUUID();
        TemplateRequest request = new TemplateRequest("CODE", UUID.randomUUID().toString(), "Sub", "Body");
        when(templateRepository.findActiveById(templateId)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.update(templateId.toString(), request))
            .expectError(ResourceNotFoundException.class)
            .verify();
    }

    @Test
    public void testDelete_NotFound() {
        UUID templateId = UUID.randomUUID();
        when(templateRepository.findActiveById(templateId)).thenReturn(Mono.empty());

        StepVerifier.create(templateService.delete(templateId.toString()))
            .expectError(ResourceNotFoundException.class)
            .verify();
    }

    @Test
    public void testFindById_InvalidId() {
        String invalidId = "invalid-uuid";
        StepVerifier.create(templateService.findById(invalidId))
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    @Test
    public void testUpdate_InvalidId() {
        String invalidId = "invalid-uuid";
        TemplateRequest request = new TemplateRequest("CODE", UUID.randomUUID().toString(), "Sub", "Body");
        StepVerifier.create(templateService.update(invalidId, request))
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    @Test
    public void testDelete_InvalidId() {
        String invalidId = "invalid-uuid";
        StepVerifier.create(templateService.delete(invalidId))
            .expectError(IllegalArgumentException.class)
            .verify();
    }
}
