package com.practica.pedidos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * Controlador para redireccionar a la documentación Swagger
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public Mono<String> redirectToSwagger() {
        return Mono.just("redirect:/swagger-ui.html");
    }

    @GetMapping("/docs")
    public Mono<String> redirectToSwaggerDocs() {
        return Mono.just("redirect:/swagger-ui.html");
    }
}
