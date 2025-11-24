package com.practica.pedidos.controller;

import com.practica.pedidos.dto.PedidoDTO;
import com.practica.pedidos.service.PedidoService;
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
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(PedidoController.class)
@Import(com.practica.pedidos.config.SecurityConfig.class)
class PedidoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PedidoService pedidoService;

    private PedidoDTO pedidoDTO1;
    private PedidoDTO pedidoDTO2;

    @BeforeEach
    void setUp() {
        pedidoDTO1 = new PedidoDTO();
        pedidoDTO1.setId(1L);
        pedidoDTO1.setCliente("cliente-123");
        pedidoDTO1.setEstado("PENDIENTE");
        pedidoDTO1.setTotal(1500.0);
        pedidoDTO1.setFecha(LocalDateTime.now());
        pedidoDTO1.setDetalles(new ArrayList<>());

        pedidoDTO2 = new PedidoDTO();
        pedidoDTO2.setId(2L);
        pedidoDTO2.setCliente("cliente-456");
        pedidoDTO2.setEstado("COMPLETADO");
        pedidoDTO2.setTotal(800.0);
        pedidoDTO2.setFecha(LocalDateTime.now());
        pedidoDTO2.setDetalles(new ArrayList<>());
    }

    @Test
    @WithMockUser
    void testGetAll_ShouldReturnAllPedidos() {
        when(pedidoService.getAll()).thenReturn(Flux.just(pedidoDTO1, pedidoDTO2));

        webTestClient.get()
                .uri("/api/pedidos")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(PedidoDTO.class)
                .hasSize(2);
    }

    @Test
    @WithMockUser
    void testGetById_ShouldReturnPedido() {
        when(pedidoService.getById(1L)).thenReturn(Mono.just(pedidoDTO1));

        webTestClient.get()
                .uri("/api/pedidos/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PedidoDTO.class)
                .value(pedido -> {
                    assert pedido.getId().equals(1L);
                    assert pedido.getCliente().equals("cliente-123");
                });
    }

    @Test
    @WithMockUser
    void testGetById_NotFound_ShouldReturn404() {
        when(pedidoService.getById(999L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/pedidos/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    void testCreate_ShouldReturnCreatedPedido() {
        PedidoDTO newPedido = new PedidoDTO();
        newPedido.setCliente("cliente-789");
        newPedido.setDetalles(new ArrayList<>());

        PedidoDTO createdPedido = new PedidoDTO();
        createdPedido.setId(3L);
        createdPedido.setCliente("cliente-789");
        createdPedido.setEstado("PENDIENTE");
        createdPedido.setTotal(500.0);
        createdPedido.setFecha(LocalDateTime.now());
        createdPedido.setDetalles(new ArrayList<>());

        when(pedidoService.create(any(PedidoDTO.class))).thenReturn(Mono.just(createdPedido));

        webTestClient.post()
                .uri("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newPedido)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PedidoDTO.class)
                .value(pedido -> {
                    assert pedido.getId().equals(3L);
                    assert pedido.getCliente().equals("cliente-789");
                });
    }

    @Test
    @WithMockUser
    void testUpdateEstado_ShouldReturnUpdatedPedido() {
        when(pedidoService.updateStatus(eq(1L), eq("PROCESADO"))).thenReturn(Mono.just(pedidoDTO1));

        webTestClient.patch()
                .uri("/api/pedidos/1/estado?estado=PROCESADO")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PedidoDTO.class);
    }
}
