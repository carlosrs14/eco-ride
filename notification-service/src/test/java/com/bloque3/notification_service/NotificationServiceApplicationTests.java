package com.bloque3.notification_service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest()
@ActiveProfiles("test")
class NotificationServiceApplicationTests {
	
    @MockitoBean
	private JavaMailSender mailSender;


	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
