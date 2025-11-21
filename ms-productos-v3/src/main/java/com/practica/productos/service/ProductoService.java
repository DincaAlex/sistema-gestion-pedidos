package com.practica.productos.service;

import com.practica.productos.model.ProductoModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<ProductoModel> listar(Boolean onlyActive);

    Mono<ProductoModel> obtenerPorId(Long id);

    Mono<ProductoModel> crear(ProductoModel producto);

    Mono<ProductoModel> actualizar(Long id, ProductoModel producto);

    Mono<Void> eliminar(Long id);

    Mono<Void> actualizarStock(Long id, Integer cantidad);

    Flux<ProductoModel> obtenerProductosBajoStock(Integer minimo);
}
