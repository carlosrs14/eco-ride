package com.bloque3.notification_service.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bloque3.notification_service.models.Template;

import reactor.core.publisher.Mono;

@Repository
public interface TemplateRepository extends ReactiveCrudRepository<Template, UUID>{
    Mono<Template> findByCode(String code); 

    @Query("SELECT * FROM templates WHERE id = :id AND is_active = TRUE")
    Mono<Template> findActiveById(UUID id);
}
