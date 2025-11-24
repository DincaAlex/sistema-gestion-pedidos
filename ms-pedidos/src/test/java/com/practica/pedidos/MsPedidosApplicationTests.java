package com.practica.pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	excludeAutoConfiguration = {
		ReactiveSecurityAutoConfiguration.class,
		ReactiveOAuth2ResourceServerAutoConfiguration.class
	}
)
@ActiveProfiles("test")
class MsPedidosApplicationTests {

	@Test
	void contextLoads() {
	}

}
