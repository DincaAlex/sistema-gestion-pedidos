package com.practica.pedidos.client;

import com.practica.pedidos.dto.ProductoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductoClient {
    private final WebClient webClient;

    public ProductoClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("${ms-productos.url}").build();
    }

    public Mono<ProductoDTO> obtenerProducto(Long id) {
        return webClient.get()
                .uri("/api/productos/{id}", id)
                .retrieve()
                .bodyToMono(ProductoDTO.class);
    }

    public Mono<Void> actualizarStock(Long id, Integer cantidad) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/productos/{id}/stock")
                        .queryParam("cantidad", cantidad)
                        .build(id))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
