package com.practica.productos.controller;

import com.practica.productos.adapter.rest.model.ProductoCreateRequest;
import com.practica.productos.adapter.rest.model.ProductoDto;
import com.practica.productos.adapter.rest.model.ProductoUpdateRequest;
import com.practica.productos.converter.ProductoCreateRequestToModelConverter;
import com.practica.productos.converter.ProductoModelToProductoDtoConverter;
import com.practica.productos.converter.ProductoUpdateRequestToModelConverter;
import com.practica.productos.model.ProductoModel;
import com.practica.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductosController.class)
@Import(com.practica.productos.config.SecurityConfig.class)
class ProductosControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private ProductoModelToProductoDtoConverter toDtoConverter;

    @MockBean
    private ProductoCreateRequestToModelConverter createConverter;

    @MockBean
    private ProductoUpdateRequestToModelConverter updateConverter;

    private ProductoModel productoModel1;
    private ProductoDto productoDto1;

    @BeforeEach
    void setUp() {
        productoModel1 = ProductoModel.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop HP")
                .precio(1000.0)
                .stock(10)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        productoDto1 = new ProductoDto();
        productoDto1.setId(1L);
        productoDto1.setNombre("Laptop");
        productoDto1.setDescripcion("Laptop HP");
        productoDto1.setPrecio(1000.0);
        productoDto1.setStock(10);
        productoDto1.setActivo(true);
        productoDto1.setFechaCreacion(OffsetDateTime.now());
    }

    @Test
    @WithMockUser
    void testListProductos_ShouldReturnSuccessResponse() {
        when(productoService.listar(anyBoolean())).thenReturn(Flux.just(productoModel1));
        when(toDtoConverter.convert(any(ProductoModel.class))).thenReturn(productoDto1);

        webTestClient.get()
                .uri("/api/v3/productos")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").isArray();
    }

    @Test
    @WithMockUser
    void testGetProductoById_ShouldReturnProduct() {
        when(productoService.obtenerPorId(1L)).thenReturn(Mono.just(productoModel1));
        when(toDtoConverter.convert(any(ProductoModel.class))).thenReturn(productoDto1);

        webTestClient.get()
                .uri("/api/v3/productos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1);
    }

    @Test
    @WithMockUser
    void testCreateProducto_ShouldReturnCreatedProduct() {
        ProductoCreateRequest request = new ProductoCreateRequest();
        request.setNombre("Nuevo Producto");
        request.setPrecio(500.0);
        request.setStock(20);

        when(createConverter.convert(any())).thenReturn(productoModel1);
        when(productoService.crear(any(ProductoModel.class))).thenReturn(Mono.just(productoModel1));
        when(toDtoConverter.convert(any(ProductoModel.class))).thenReturn(productoDto1);

        webTestClient.post()
                .uri("/api/v3/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    @WithMockUser
    void testUpdateProducto_ShouldReturnUpdatedProduct() {
        ProductoUpdateRequest request = new ProductoUpdateRequest();
        request.setNombre("Producto Actualizado");
        request.setPrecio(600.0);

        when(updateConverter.convert(any())).thenReturn(productoModel1);
        when(productoService.actualizar(eq(1L), any(ProductoModel.class))).thenReturn(Mono.just(productoModel1));
        when(toDtoConverter.convert(any(ProductoModel.class))).thenReturn(productoDto1);

        webTestClient.put()
                .uri("/api/v3/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    @WithMockUser
    void testDeleteProducto_ShouldReturnSuccess() {
        when(productoService.eliminar(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v3/productos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }
}
