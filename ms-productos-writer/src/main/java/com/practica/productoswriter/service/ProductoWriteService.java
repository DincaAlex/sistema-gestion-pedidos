package com.practica.productoswriter.service;

import com.practica.productoswriter.entity.Producto;
import com.practica.productoswriter.entity.ProductoDTO;
import com.practica.productoswriter.event.ProductoCreatedEvent;
import com.practica.productoswriter.event.ProductoDeletedEvent;
import com.practica.productoswriter.event.ProductoUpdatedEvent;
import com.practica.productoswriter.event.StockUpdatedEvent;
import com.practica.productoswriter.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoWriteService {

    private final ProductoRepository productoRepository;

    /**
     * Procesa evento de creación de producto
     */
    public Mono<Producto> procesarCreacion(ProductoCreatedEvent event) {
        log.info("Procesando creación de producto - EventId: {}, CorrelationId: {}",
                 event.getEventId(), event.getCorrelationId());

        ProductoDTO dto = event.getPayload();
        Producto producto = convertToEntity(dto);

        return productoRepository.save(producto)
                .doOnSuccess(saved -> log.info("Producto creado exitosamente - ID: {}, CorrelationId: {}",
                                               saved.getId(), event.getCorrelationId()))
                .doOnError(error -> log.error("Error al crear producto - CorrelationId: {}",
                                              event.getCorrelationId(), error));
    }

    /**
     * Procesa evento de actualización de producto
     */
    public Mono<Producto> procesarActualizacion(ProductoUpdatedEvent event) {
        log.info("Procesando actualización de producto {} - EventId: {}, CorrelationId: {}",
                 event.getProductoId(), event.getEventId(), event.getCorrelationId());

        ProductoDTO dto = event.getPayload();

        return productoRepository.findById(event.getProductoId())
                .flatMap(existing -> {
                    existing.setNombre(dto.getNombre());
                    existing.setDescripcion(dto.getDescripcion());
                    existing.setPrecio(dto.getPrecio());
                    existing.setStock(dto.getStock());
                    existing.setActivo(dto.getActivo());
                    return productoRepository.save(existing);
                })
                .doOnSuccess(updated -> log.info("Producto {} actualizado exitosamente - CorrelationId: {}",
                                                 event.getProductoId(), event.getCorrelationId()))
                .doOnError(error -> log.error("Error al actualizar producto {} - CorrelationId: {}",
                                              event.getProductoId(), event.getCorrelationId(), error));
    }

    /**
     * Procesa evento de actualización de stock
     */
    public Mono<Void> procesarActualizacionStock(StockUpdatedEvent event) {
        log.info("Procesando actualización de stock - Producto: {}, Cantidad: {}, EventId: {}, CorrelationId: {}",
                 event.getProductoId(), event.getCantidad(), event.getEventId(), event.getCorrelationId());

        return productoRepository.findById(event.getProductoId())
                .flatMap(producto -> {
                    // Validar que el cálculo del stock sea correcto
                    int nuevoStockCalculado = producto.getStock() + event.getCantidad();
                    if (nuevoStockCalculado != event.getStockNuevo()) {
                        log.warn("Discrepancia en stock calculado vs esperado - Producto: {}, Calculado: {}, Esperado: {}",
                                 event.getProductoId(), nuevoStockCalculado, event.getStockNuevo());
                    }

                    producto.setStock(event.getStockNuevo());
                    return productoRepository.save(producto);
                })
                .then()
                .doOnSuccess(v -> log.info("Stock de producto {} actualizado exitosamente - Nuevo stock: {}, CorrelationId: {}",
                                           event.getProductoId(), event.getStockNuevo(), event.getCorrelationId()))
                .doOnError(error -> log.error("Error al actualizar stock de producto {} - CorrelationId: {}",
                                              event.getProductoId(), event.getCorrelationId(), error));
    }

    /**
     * Procesa evento de eliminación de producto
     */
    public Mono<Void> procesarEliminacion(ProductoDeletedEvent event) {
        log.info("Procesando eliminación de producto {} - EventId: {}, CorrelationId: {}",
                 event.getProductoId(), event.getEventId(), event.getCorrelationId());

        return productoRepository.deleteById(event.getProductoId())
                .doOnSuccess(v -> log.info("Producto {} eliminado exitosamente - CorrelationId: {}",
                                           event.getProductoId(), event.getCorrelationId()))
                .doOnError(error -> log.error("Error al eliminar producto {} - CorrelationId: {}",
                                              event.getProductoId(), event.getCorrelationId(), error));
    }

    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return producto;
    }
}
