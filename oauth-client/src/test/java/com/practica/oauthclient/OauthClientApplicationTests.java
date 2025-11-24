package com.practica.oauthclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	excludeAutoConfiguration = {
		SecurityAutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class
	}
)
@ActiveProfiles("test")
class OauthClientApplicationTests {

	@Test
	void contextLoads() {
	}

}
