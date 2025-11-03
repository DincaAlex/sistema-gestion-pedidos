package com.practica.pedidos.repository;

import com.practica.pedidos.entity.Pedido;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface PedidoRepository extends R2dbcRepository<Pedido, Long> {
    Flux<Pedido> findByCliente(String cliente);

    Flux<Pedido> findByEstado(String estado);
}
