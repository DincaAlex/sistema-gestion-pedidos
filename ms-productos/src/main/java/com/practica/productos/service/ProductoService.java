package com.practica.productos.service;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.entity.Producto;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
import com.practica.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public Flux<ProductoDTO> listarTodos() {
        return Flux.defer(() -> Flux.fromIterable(productoRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    public Flux<ProductoDTO> listarActivos() {
        return Flux.defer(() -> Flux.fromIterable(productoRepository.findByActivoTrue()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    public Mono<ProductoDTO> buscarPorId(Long id) {
        return Mono.fromCallable(() -> productoRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    @Transactional
    public Mono<ProductoDTO> crear(ProductoDTO productoDTO) {
        return Mono.fromCallable(() -> {
                    validarProducto(productoDTO);
                    Producto producto = convertToEntity(productoDTO);
                    return productoRepository.save(producto);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    @Transactional
    public Mono<ProductoDTO> actualizar(Long id, ProductoDTO productoDTO) {
        return Mono.fromCallable(() -> {
                    Producto productoExistente = productoRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

                    validarProducto(productoDTO);
                    productoExistente.setNombre(productoDTO.getNombre());
                    productoExistente.setDescripcion(productoDTO.getDescripcion());
                    productoExistente.setPrecio(productoDTO.getPrecio());
                    productoExistente.setStock(productoDTO.getStock());
                    productoExistente.setActivo(productoDTO.getActivo());

                    return productoRepository.save(productoExistente);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    @Transactional
    public Mono<Void> eliminar(Long id) {
        return Mono.fromRunnable(() -> {
                    if (!productoRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
                    }
                    productoRepository.deleteById(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Transactional
    public Mono<Void> actualizarStock(Long id, Integer cantidad) {
        return Mono.fromRunnable(() -> {
                    Producto producto = productoRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

                    if (producto.getStock() < cantidad) {
                        throw new BadRequestException("Stock insuficiente");
                    }

                    productoRepository.actualizarStock(id, cantidad);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Flux<ProductoDTO> obtenerProductosBajoStock(Integer minimo) {
        return Flux.defer(() -> Flux.fromIterable(productoRepository.obtenerProductosBajoStock(minimo)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    private void validarProducto(ProductoDTO productoDTO) {
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
                producto.getFechaCreacion()
        );
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
