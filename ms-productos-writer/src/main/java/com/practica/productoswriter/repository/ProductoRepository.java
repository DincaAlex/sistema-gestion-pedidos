package com.practica.productoswriter.repository;

import com.practica.productoswriter.entity.Producto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductoRepository extends ReactiveCrudRepository<Producto, Long> {
    Flux<Producto> findByActivoTrue();
    Flux<Producto> findByStockLessThanEqual(Integer stock);

    // Procedimiento almacenado: actualizar_stock
    @Query("SELECT actualizar_stock(:productoId, :cantidad)")
    Mono<Void> actualizarStockConProcedimiento(Long productoId, Integer cantidad);

    // Procedimiento almacenado: productos_bajo_stock
    @Query("SELECT * FROM productos_bajo_stock(:minimo)")
    Flux<Producto> obtenerProductosBajoStockConProcedimiento(Integer minimo);
}
