package com.practica.oauthserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	excludeAutoConfiguration = {
		SecurityAutoConfiguration.class,
		OAuth2AuthorizationServerAutoConfiguration.class
	}
)
@ActiveProfiles("test")
class OauthServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
