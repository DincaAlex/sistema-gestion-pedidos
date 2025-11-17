package com.practica.productos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Controlador para redireccionar a la documentación Swagger
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Mono<Void> redirectToSwagger(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create("/swagger-ui.html"));
        return response.setComplete();
    }

    @GetMapping("/docs")
    public Mono<Void> redirectToSwaggerDocs(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        response.getHeaders().setLocation(URI.create("/swagger-ui.html"));
        return response.setComplete();
    }
}
