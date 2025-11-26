package com.bloque3.passenger_service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bloque3.passenger_service.services.impl.KeycloakServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
class PassengerServiceApplicationTests {

	@MockitoBean
	private KeycloakServiceImpl keycloakServiceImpl;
	
}
