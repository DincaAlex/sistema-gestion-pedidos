package com.practica.productos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = HomeController.class)
@Import(com.practica.productos.config.SecurityConfig.class)
@ActiveProfiles("test")
class HomeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testRedirectToSwagger_FromRoot_ShouldRedirect() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TEMPORARY_REDIRECT)
                .expectHeader().location("/swagger-ui.html");
    }

    @Test
    void testRedirectToSwagger_FromDocs_ShouldRedirect() {
        webTestClient.get()
                .uri("/docs")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TEMPORARY_REDIRECT)
                .expectHeader().location("/swagger-ui.html");
    }
}
