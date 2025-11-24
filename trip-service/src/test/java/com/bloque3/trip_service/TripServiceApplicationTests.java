package com.bloque3.trip_service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TripServiceApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
