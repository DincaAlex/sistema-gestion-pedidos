package com.practica.productos.controller;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ProductoController.class)
@Import(com.practica.productos.config.SecurityConfig.class)
@ActiveProfiles("test")
class ProductoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductoService productoService;

    private ProductoDTO productoDTO1;
    private ProductoDTO productoDTO2;

    @BeforeEach
    void setUp() {
        productoDTO1 = new ProductoDTO(1L, "Laptop", "Laptop HP", 1000.0, 10, true, LocalDateTime.now());
        productoDTO2 = new ProductoDTO(2L, "Mouse", "Mouse Logitech", 20.0, 50, true, LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void testListAll_ShouldReturnAllProducts() {
        when(productoService.getAll()).thenReturn(Flux.just(productoDTO1, productoDTO2));

        webTestClient.get()
                .uri("/api/v1/productos")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductoDTO.class)
                .hasSize(2);
    }

    @Test
    @WithMockUser
    void testListAll_OnlyActive_ShouldReturnActiveProducts() {
        when(productoService.getActive()).thenReturn(Flux.just(productoDTO1, productoDTO2));

        webTestClient.get()
                .uri("/api/v1/productos?onlyActive=true")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductoDTO.class)
                .hasSize(2);
    }

    @Test
    @WithMockUser
    void testGetById_ShouldReturnProduct() {
        when(productoService.getById(1L)).thenReturn(Mono.just(productoDTO1));

        webTestClient.get()
                .uri("/api/v1/productos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductoDTO.class)
                .value(producto -> {
                    assert producto.getId().equals(1L);
                    assert producto.getNombre().equals("Laptop");
                });
    }

    @Test
    @WithMockUser
    void testCreate_ShouldReturnCreatedProduct() {
        ProductoDTO newProducto = new ProductoDTO(null, "Teclado", "Teclado Mecanico", 150.0, 20, true, null);
        ProductoDTO createdProducto = new ProductoDTO(3L, "Teclado", "Teclado Mecanico", 150.0, 20, true, LocalDateTime.now());

        when(productoService.create(any(ProductoDTO.class))).thenReturn(Mono.just(createdProducto));

        webTestClient.post()
                .uri("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProducto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductoDTO.class)
                .value(producto -> {
                    assert producto.getId().equals(3L);
                    assert producto.getNombre().equals("Teclado");
                });
    }

    @Test
    @WithMockUser
    void testUpdate_ShouldReturnUpdatedProduct() {
        ProductoDTO updateDTO = new ProductoDTO(1L, "Laptop Updated", "Laptop HP Updated", 1200.0, 15, true, null);

        when(productoService.update(eq(1L), any(ProductoDTO.class))).thenReturn(Mono.just(updateDTO));

        webTestClient.put()
                .uri("/api/v1/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductoDTO.class)
                .value(producto -> {
                    assert producto.getNombre().equals("Laptop Updated");
                    assert producto.getPrecio().equals(1200.0);
                });
    }

    @Test
    @WithMockUser
    void testDelete_ShouldReturnNoContent() {
        when(productoService.delete(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/productos/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithMockUser
    void testUpdateStock_ShouldReturnOk() {
        when(productoService.updateStock(1L, 5)).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/v1/productos/1/stock?cantidad=5")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser
    void testGetLowStock_ShouldReturnProductsWithLowStock() {
        when(productoService.getProductsLowStock(10)).thenReturn(Flux.just(productoDTO1));

        webTestClient.get()
                .uri("/api/v1/productos/low-stock?minimo=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductoDTO.class)
                .hasSize(1);
    }

    @Test
    @WithMockUser
    void testGetLowStock_DefaultMinimo_ShouldReturnProducts() {
        when(productoService.getProductsLowStock(10)).thenReturn(Flux.just(productoDTO1));

        webTestClient.get()
                .uri("/api/v1/productos/low-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductoDTO.class);
    }
}
