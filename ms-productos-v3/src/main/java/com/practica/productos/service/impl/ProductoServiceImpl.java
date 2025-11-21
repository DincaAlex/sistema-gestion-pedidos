package com.practica.productos.service.impl;

import com.practica.productos.event.*;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
import com.practica.productos.kafka.ProductoEventProducer;
import com.practica.productos.model.ProductoModel;
import com.practica.productos.repository.ProductoRepository;
import com.practica.productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;
    private final ProductoEventProducer eventProducer;

    @Override
    public Flux<ProductoModel> listar(Boolean onlyActive) {
        if (onlyActive != null && onlyActive) {
            return repository.findByActivoTrue();
        }
        return repository.findAll();
    }

    @Override
    public Mono<ProductoModel> obtenerPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id)));
    }

    @Override
    public Mono<ProductoModel> crear(ProductoModel producto) {
        validarProducto(producto);

        ProductoCreatedEvent event = ProductoCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PRODUCTO_CREATED")
                .timestamp(LocalDateTime.now())
                .correlationId(UUID.randomUUID().toString())
                .version("1.0")
                .metadata(createMetadata())
                .payload(convertToDTO(producto))
                .build();

        return eventProducer.publishEvent("new-producto", event)
                .thenReturn(producto);
    }

    @Override
    public Mono<ProductoModel> actualizar(Long id, ProductoModel producto) {
        validarProducto(producto);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id)))
                .flatMap(existing -> {
                    ProductoUpdatedEvent event = ProductoUpdatedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("PRODUCTO_UPDATED")
                            .timestamp(LocalDateTime.now())
                            .correlationId(UUID.randomUUID().toString())
                            .version("1.0")
                            .metadata(createMetadata())
                            .productoId(id)
                            .payload(convertToDTO(producto))
                            .build();

                    return eventProducer.publishEvent(id, event)
                            .thenReturn(producto);
                });
    }

    @Override
    public Mono<Void> eliminar(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id)))
                .flatMap(producto -> {
                    ProductoDeletedEvent event = ProductoDeletedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("PRODUCTO_DELETED")
                            .timestamp(LocalDateTime.now())
                            .correlationId(UUID.randomUUID().toString())
                            .version("1.0")
                            .metadata(createMetadata())
                            .productoId(id)
                            .build();

                    return eventProducer.publishEvent(id, event);
                });
    }

    @Override
    public Mono<Void> actualizarStock(Long id, Integer cantidad) {
        if (cantidad == 0) {
            return Mono.error(new BadRequestException("La cantidad no puede ser cero"));
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id)))
                .flatMap(producto -> {
                    Integer stockAnterior = producto.getStock();
                    Integer stockNuevo = stockAnterior + cantidad;

                    if (stockNuevo < 0) {
                        return Mono.error(new BadRequestException(
                                String.format("Stock insuficiente. Actual: %d, solicitado: %d",
                                        stockAnterior, Math.abs(cantidad))));
                    }

                    StockUpdatedEvent event = StockUpdatedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("STOCK_UPDATED")
                            .timestamp(LocalDateTime.now())
                            .correlationId(UUID.randomUUID().toString())
                            .version("1.0")
                            .metadata(createMetadata())
                            .productoId(id)
                            .cantidad(cantidad)
                            .stockAnterior(stockAnterior)
                            .stockNuevo(stockNuevo)
                            .build();

                    return eventProducer.publishEvent(id, event);
                });
    }

    @Override
    public Flux<ProductoModel> obtenerProductosBajoStock(Integer minimo) {
        Integer minimoFinal = minimo != null ? minimo : 10;
        return repository.obtenerProductosBajoStockConProcedimiento(minimoFinal);
    }

    private void validarProducto(ProductoModel producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            throw new BadRequestException("El precio debe ser mayor a 0");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }
    }

    private ProductoEvent.EventMetadata createMetadata() {
        ProductoEvent.EventMetadata metadata = new ProductoEvent.EventMetadata();
        metadata.setSource("ms-productos-v3");
        return metadata;
    }

    private com.practica.productos.event.ProductoDTO convertToDTO(ProductoModel model) {
        com.practica.productos.event.ProductoDTO dto = new com.practica.productos.event.ProductoDTO();
        dto.setId(model.getId());
        dto.setNombre(model.getNombre());
        dto.setDescripcion(model.getDescripcion());
        dto.setPrecio(model.getPrecio());
        dto.setStock(model.getStock());
        dto.setActivo(model.getActivo());
        dto.setFechaCreacion(model.getFechaCreacion());
        return dto;
    }
}
