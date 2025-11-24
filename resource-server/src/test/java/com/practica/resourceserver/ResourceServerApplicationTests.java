package com.practica.resourceserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	excludeAutoConfiguration = {
		SecurityAutoConfiguration.class,
		OAuth2ResourceServerAutoConfiguration.class
	}
)
@ActiveProfiles("test")
class ResourceServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
