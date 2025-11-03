package com.practica.pedidos.service;

import com.practica.pedidos.client.ProductoClient;
import com.practica.pedidos.dto.DetallePedidoDTO;
import com.practica.pedidos.dto.PedidoDTO;
import com.practica.pedidos.entity.DetallePedido;
import com.practica.pedidos.entity.Pedido;
import com.practica.pedidos.exception.BadRequestException;
import com.practica.pedidos.exception.ResourceNotFoundException;
import com.practica.pedidos.repository.DetallePedidoRepository;
import com.practica.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoClient productoClient;

    public Flux<PedidoDTO> getAll() {
        return pedidoRepository.findAll()
                .flatMap(this::enrichPedidoWithDetalles)
                .onErrorResume(error ->
                        Flux.error(new RuntimeException("Error al listar pedidos: " + error.getMessage(), error)));
    }

    public Mono<PedidoDTO> getById(Long id) {
        return pedidoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Pedido no encontrado con id: " + id)))
                .flatMap(this::enrichPedidoWithDetalles);
    }

    @Transactional
    public Mono<PedidoDTO> create(PedidoDTO pedidoDTO) {
        return validarPedido(pedidoDTO)
                .then(validarProductos(pedidoDTO))
                .flatMap(validatedDTO -> {
                    Pedido pedido = new Pedido();
                    pedido.setCliente(validatedDTO.getCliente());
                    pedido.setFecha(LocalDateTime.now());
                    pedido.setEstado("PENDIENTE");
                    pedido.setTotal(calcularTotal(validatedDTO.getDetalles()));

                    return pedidoRepository.save(pedido)
                            .flatMap(savedPedido -> guardarDetalles(savedPedido, validatedDTO.getDetalles())
                                    .then(actualizarStockProductos(validatedDTO.getDetalles()))
                                    .then(enrichPedidoWithDetalles(savedPedido)));
                });
    }

    @Transactional
    public Mono<PedidoDTO> updateStatus(Long id, String nuevoEstado) {
        if (!List.of("PENDIENTE", "PROCESADO", "CANCELADO").contains(nuevoEstado)) {
            return Mono.error(new BadRequestException("Estado inválido"));
        }

        return pedidoRepository.findById(id)
                .switchIfEmpty(Mono.error(new BadRequestException("Pedido no encontrado")))
                .flatMap(pedido -> {
                    pedido.setEstado(nuevoEstado);
                    return pedidoRepository.save(pedido);
                })
                .flatMap(this::enrichPedidoWithDetalles);
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return pedidoRepository.findById(id)
                .switchIfEmpty(Mono.error(new BadRequestException("Pedido no encontrado")))
                .flatMap(pedido -> detallePedidoRepository.deleteAll(pedido.getDetalles())
                        .then(pedidoRepository.delete(pedido)));
    }

    private Mono<PedidoDTO> enrichPedidoWithDetalles(Pedido pedido) {
        return detallePedidoRepository.findByPedidoId(pedido.getId())
                .collectList()
                .map(detalles -> convertToDTO(pedido, detalles))
                .onErrorResume(error -> {
                    // Si falla la recuperación de detalles, devolver pedido sin detalles
                    return Mono.error(new RuntimeException("Error al obtener detalles del pedido: " + error.getMessage(), error));
                });
    }

    private Mono<PedidoDTO> validarPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getCliente() == null || pedidoDTO.getCliente().trim().isEmpty()) {
            return Mono.error(new BadRequestException("El cliente es obligatorio"));
        }
        if (pedidoDTO.getDetalles() == null || pedidoDTO.getDetalles().isEmpty()) {
            return Mono.error(new BadRequestException("El pedido debe tener al menos un producto"));
        }
        return Mono.just(pedidoDTO);
    }

    private Mono<PedidoDTO> validarProductos(PedidoDTO pedidoDTO) {
        return Flux.fromIterable(pedidoDTO.getDetalles())
                .flatMap(detalle -> productoClient.obtenerProducto(detalle.getProductoId())
                        .switchIfEmpty(Mono
                                .error(new BadRequestException("Producto no encontrado: " + detalle.getProductoId())))
                        .filter(producto -> producto.getActivo() && producto.getStock() >= detalle.getCantidad())
                        .switchIfEmpty(Mono.error(new BadRequestException("Producto inactivo o stock insuficiente")))
                        .map(producto -> {
                            detalle.setPrecioUnitario(producto.getPrecio());
                            return detalle;
                        }))
                .collectList()
                .map(detalles -> {
                    pedidoDTO.setDetalles(detalles);
                    return pedidoDTO;
                });
    }

    private Mono<Void> guardarDetalles(Pedido pedido, List<DetallePedidoDTO> detallesDTO) {
        return Flux.fromIterable(detallesDTO)
                .map(detalleDTO -> {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setPedidoId(pedido.getId());
                    detalle.setProductoId(detalleDTO.getProductoId());
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                    return detalle;
                })
                .flatMap(detallePedidoRepository::save)
                .then();
    }

    private Mono<Void> actualizarStockProductos(List<DetallePedidoDTO> detalles) {
        return Flux.fromIterable(detalles)
                .flatMap(detalle -> productoClient.actualizarStock(
                                detalle.getProductoId(),
                                -detalle.getCantidad())
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                .maxBackoff(Duration.ofSeconds(5)))
                        .onErrorResume(error ->
                                Mono.error(new RuntimeException("Error al actualizar stock del producto "
                                        + detalle.getProductoId() + ": " + error.getMessage(), error))))
                .then();
    }

    private double calcularTotal(List<DetallePedidoDTO> detalles) {
        return detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
    }

    private PedidoDTO convertToDTO(Pedido pedido, List<DetallePedido> detalles) {
        List<DetallePedidoDTO> detallesDTO = detalles.stream()
                .map(d -> new DetallePedidoDTO(d.getId(), d.getProductoId(), d.getCantidad(), d.getPrecioUnitario()))
                .toList();

        return new PedidoDTO(
                pedido.getId(),
                pedido.getCliente(),
                pedido.getFecha(),
                pedido.getTotal(),
                pedido.getEstado(),
                detallesDTO);
    }
}
