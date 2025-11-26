package com.bloque3.passenger_service.services.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.bloque3.passenger_service.controllers.dtos.request.PassengerRequestDTO;
import com.bloque3.passenger_service.services.KeycloakService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.url}")
    private String keycloakUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.admin-realm}")
    private String adminRealm;
    
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;
    

    @SuppressWarnings("null")
    private WebClient webClient() {
        return WebClient.builder().baseUrl(keycloakUrl).build();
    }
    

    private Mono<String> getAdminAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);

        MediaType mediaType = MediaType.APPLICATION_FORM_URLENCODED;
        if (mediaType == null) throw new NullPointerException("Media type is null");
        
        return webClient().post()
            .uri("/realms/{adminRealm}/protocol/openid-connect/token", adminRealm)
            .contentType(mediaType)
            .body(BodyInserters.fromFormData(form))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(node -> node.get("access_token").asText());
    }


    @Override
    public Mono<String> createuser(PassengerRequestDTO requestDTO) {
        return getAdminAccessToken()
            .flatMap(token -> {
                Map<String, Object> payload = new HashMap<>();
                payload.put("username", requestDTO.username());
                payload.put("firstName", requestDTO.name());
                payload.put("lastName", requestDTO.name());
                payload.put("email", requestDTO.email());
                payload.put("enabled", true);
                payload.put("emailVerified", true);

                Map<String, Object> credential = new HashMap<>();
                credential.put("type", "password");
                credential.put("value", requestDTO.password());
                credential.put("temporary", false);
                payload.put("credentials", Collections.singletonList(credential));
                
                MediaType mediaType = MediaType.APPLICATION_JSON;
                if (mediaType == null) throw new NullPointerException("Media type is null");

                return webClient().post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(mediaType)
                    .accept(mediaType)
                    .body(BodyInserters.fromValue(payload))
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful() || response.statusCode().equals(HttpStatus.CREATED)) {
                            List<String> loc = response.headers().asHttpHeaders().get(HttpHeaders.LOCATION);
                            if (loc != null && !loc.isEmpty()) {
                                String location = loc.get(0);
                                String[] parts = location.split("/");
                                String userId = parts[parts.length - 1];
                                return Mono.just(userId);
                            }
                            return webClient().get()
                                .uri(uriBuilder -> uriBuilder
                                    .path("/admin/realms/{realm}/users")
                                    .queryParam("username", requestDTO.username())
                                    .build(realm))
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<List<Map<String,Object>>>(){})
                                .flatMap(list -> {
                                    if (list != null && !list.isEmpty()) {
                                        Object id = list.get(0).get("id");
                                        if (id != null) return Mono.just(id.toString());
                                    }
                                    return Mono.error(new RuntimeException("Unable to obtain created user id from Keycloak"));
                                });
                        } else {
                            return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Keycloak create user error: " + body)));
                        }
                    });
            });
    }
}
