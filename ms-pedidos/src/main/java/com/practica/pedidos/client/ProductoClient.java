package com.practica.pedidos.client;

import com.practica.pedidos.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ProductoClient {
    private final WebClient webClient;

    public ProductoClient(WebClient.Builder webClientBuilder,
                          @Value("${ms-productos.url:http://localhost:8081}") String productosUrl) {
        this.webClient = webClientBuilder.baseUrl(productosUrl).build();
    }

    public Mono<ProductoDTO> obtenerProducto(Long id) {
        return webClient.get()
                .uri("/api/productos/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        response -> Mono.error(new RuntimeException("Producto no encontrado con id: " + id)))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error del servidor al obtener producto: " + id)))
                .bodyToMono(ProductoDTO.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    if (error instanceof java.util.concurrent.TimeoutException) {
                        return Mono.error(new RuntimeException("Timeout al obtener producto: " + id, error));
                    }
                    return Mono.error(error);
                });
    }

    public Mono<Void> actualizarStock(Long id, Integer cantidad) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/productos/{id}/stock")
                        .queryParam("cantidad", cantidad)
                        .build(id))
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        response -> Mono.error(new RuntimeException("Producto no encontrado con id: " + id)))
                .onStatus(status -> status.value() == HttpStatus.BAD_REQUEST.value(),
                        response -> Mono.error(new RuntimeException("Stock insuficiente para producto: " + id)))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error del servidor al actualizar stock: " + id)))
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    if (error instanceof java.util.concurrent.TimeoutException) {
                        return Mono.error(new RuntimeException("Timeout al actualizar stock: " + id, error));
                    }
                    return Mono.error(error);
                });
    }
}
