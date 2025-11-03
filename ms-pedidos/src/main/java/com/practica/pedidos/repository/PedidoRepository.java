package com.practica.pedidos.repository;

import com.practica.pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(String cliente);
    List<Pedido> findByEstado(String estado);
}
