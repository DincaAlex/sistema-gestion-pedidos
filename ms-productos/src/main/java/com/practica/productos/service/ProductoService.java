package com.practica.productos.service;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.entity.Producto;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
import com.practica.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public Flux<ProductoDTO> getAll() {
        return productoRepository.findAll()
                .map(this::convertToDTO)
                .onErrorResume(error ->
                        Flux.error(new RuntimeException("Error al obtener productos: " + error.getMessage(), error)));
    }

    public Flux<ProductoDTO> getActive() {
        return productoRepository.findByActivoTrue()
                .map(this::convertToDTO)
                .onErrorResume(error ->
                        Flux.error(new RuntimeException("Error al obtener productos activos: " + error.getMessage(), error)));
    }

    public Mono<ProductoDTO> getById(Long id) {
        return productoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado con id: " + id)))
                .map(this::convertToDTO);
    }

    public Mono<ProductoDTO> create(ProductoDTO productoDTO) {
        return Mono.just(productoDTO)
                .doOnNext(this::validateProduct)
                .map(this::convertToEntity)
                .flatMap(productoRepository::save)
                .map(this::convertToDTO);
    }

    public Mono<ProductoDTO> update(Long id, ProductoDTO productoDTO) {
        return Mono.just(productoDTO)
                .doOnNext(this::validateProduct)
                .flatMap(dto -> productoRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado con id: " + id)))
                        .flatMap(existing -> {
                            existing.setNombre(dto.getNombre());
                            existing.setDescripcion(dto.getDescripcion());
                            existing.setPrecio(dto.getPrecio());
                            existing.setStock(dto.getStock());
                            existing.setActivo(dto.getActivo());
                            return productoRepository.save(existing);
                        }))
                .map(this::convertToDTO);
    }

    public Mono<Void> delete(Long id) {
        return productoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Producto no encontrado con id: " + id));
                    }
                    return productoRepository.deleteById(id);
                });
    }

    public Mono<Void> updateStock(Long id, Integer cantidad) {
        return Mono.just(cantidad)
                .filter(cant -> cant != 0)
                .switchIfEmpty(Mono.error(new BadRequestException("La cantidad no puede ser cero")))
                .flatMap(cant -> productoRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado con id: " + id)))
                        .flatMap(producto -> {
                            int nuevoStock = producto.getStock() + cant;
                            if (nuevoStock < 0) {
                                return Mono.error(new BadRequestException("Stock insuficiente. Actual: " + producto.getStock() + ", solicitado: " + Math.abs(cant)));
                            }
                            // Usar procedimiento almacenado para actualizar el stock
                            // Nota: el procedimiento resta la cantidad, por eso enviamos -cant para sumar
                            return productoRepository.actualizarStockConProcedimiento(id, -cant)
                                    .onErrorResume(error ->
                                            Mono.error(new RuntimeException("Error al ejecutar procedimiento de actualización de stock: " + error.getMessage(), error)));
                        }))
                .then();
    }

    public Flux<ProductoDTO> getProductsLowStock(Integer minimo) {
        if (minimo == null) {
            minimo = 10;
        }
        // Usar procedimiento almacenado para obtener productos con stock bajo
        return productoRepository.obtenerProductosBajoStockConProcedimiento(minimo)
                .map(this::convertToDTO)
                .onErrorResume(error ->
                        Flux.error(new RuntimeException("Error al ejecutar procedimiento de productos con stock bajo: " + error.getMessage(), error)));
    }

    private void validateProduct(ProductoDTO productoDTO) {
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del producto es obligatorio");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() <= 0) {
            throw new BadRequestException("El precio debe ser mayor a 0");
        }
        if (productoDTO.getStock() == null || productoDTO.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }
    }

    private ProductoDTO convertToDTO(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getActivo(),
                producto.getFechaCreacion());
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