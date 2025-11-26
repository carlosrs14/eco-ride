package com.bloque3.notification_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bloque3.notification_service.controllers.request.TemplateRequest;
import com.bloque3.notification_service.controllers.response.TemplateResponse;
import com.bloque3.notification_service.services.TemplateService;

import reactor.core.publisher.Mono;

@WebFluxTest(TemplateController.class)
public class TemplateControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TemplateService templateService;

    @SuppressWarnings("null")
    @Test
    public void testCreateTemplate_Success() {
        UUID channelId = UUID.randomUUID();
        TemplateRequest request = new TemplateRequest("CODE-01", channelId.toString(), "Subject", "Body");
        TemplateResponse response = new TemplateResponse(UUID.randomUUID().toString(), "CODE-01", channelId.toString(), "Subject", "Body");

        when(templateService.create(any(TemplateRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TemplateResponse.class)
                .isEqualTo(response);
    }

    @Test
    public void testFindById_Success() {
        UUID templateId = UUID.randomUUID();
        TemplateResponse response = new TemplateResponse(templateId.toString(), "CODE", null, null, null);
        when(templateService.findById(templateId.toString())).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/templates/{id}", templateId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TemplateResponse.class)
                .isEqualTo(response);
    }

    @Test
    public void testFindByCode_Success() {
        String code = "TEST-CODE";
        TemplateResponse response = new TemplateResponse(UUID.randomUUID().toString(), code, null, null, null);
        when(templateService.findByCode(code)).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/templates/code/{code}", code)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TemplateResponse.class)
                .isEqualTo(response);
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdate_Success() {
        UUID templateId = UUID.randomUUID();
        TemplateRequest request = new TemplateRequest("CODE", UUID.randomUUID().toString(), "Sub", "Body");
        TemplateResponse response = new TemplateResponse(templateId.toString(), "CODE", null, null, null);
        when(templateService.update(templateId.toString(), request)).thenReturn(Mono.just(response));

        webTestClient.put().uri("/api/v1/templates/{id}", templateId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TemplateResponse.class)
                .isEqualTo(response);
    }

    @Test
    public void testDelete_Success() {
        UUID templateId = UUID.randomUUID();
        when(templateService.delete(templateId.toString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/templates/{id}", templateId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testDelete_NotFound() {
        UUID templateId = UUID.randomUUID();
        when(templateService.delete(templateId.toString())).thenReturn(Mono.error(new RuntimeException()));

        webTestClient.delete().uri("/api/v1/templates/{id}", templateId)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
