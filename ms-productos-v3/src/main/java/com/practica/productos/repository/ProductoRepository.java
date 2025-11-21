package com.practica.productos.repository;

import com.practica.productos.model.ProductoModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductoRepository extends ReactiveCrudRepository<ProductoModel, Long> {

    Flux<ProductoModel> findByActivoTrue();

    Flux<ProductoModel> findByStockLessThanEqual(Integer stock);

    @Query("SELECT actualizar_stock(:productoId, :cantidad)")
    Mono<Void> actualizarStockConProcedimiento(Long productoId, Integer cantidad);

    @Query("SELECT * FROM productos_bajo_stock(:minimo)")
    Flux<ProductoModel> obtenerProductosBajoStockConProcedimiento(Integer minimo);
}
