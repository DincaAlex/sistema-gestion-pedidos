package com.practica.pedidos.repository;

import com.practica.pedidos.entity.DetallePedido;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface DetallePedidoRepository extends R2dbcRepository<DetallePedido, Long> {
    Flux<DetallePedido> findByPedidoId(Long pedidoId);
}
