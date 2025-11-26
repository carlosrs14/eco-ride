package com.bloque3.notification_service.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bloque3.notification_service.controllers.request.TemplateRequest;
import com.bloque3.notification_service.controllers.response.TemplateResponse;
import com.bloque3.notification_service.services.TemplateService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {
    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public Mono<TemplateResponse> create(@Valid @RequestBody TemplateRequest templateRequest) {
        return templateService.create(templateRequest);
    }

    @GetMapping("/{id}")
    public Mono<TemplateResponse> findById(@PathVariable String id) {
        return templateService.findById(id);
    }

    @GetMapping("/code/{code}")
    public Mono<TemplateResponse> findByCode(@PathVariable String code) {
        return templateService.findByCode(code);
    }

    @PutMapping("/{id}")
    public Mono<TemplateResponse> update(@PathVariable String id, @Valid @RequestBody TemplateRequest templateRequest) {
        return templateService.update(id, templateRequest);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return templateService.delete(id);
    }    
}
