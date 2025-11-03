package com.practica.pedidos.service;

import com.practica.pedidos.client.ProductoClient;
import com.practica.pedidos.dto.DetallePedidoDTO;
import com.practica.pedidos.dto.PedidoDTO;
import com.practica.pedidos.dto.ProductoDTO;
import com.practica.pedidos.entity.DetallePedido;
import com.practica.pedidos.entity.Pedido;
import com.practica.pedidos.exception.BadRequestException;
import com.practica.pedidos.exception.ResourceNotFoundException;
import com.practica.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoClient productoClient;

    public Flux<PedidoDTO> listarTodos() {
        return Flux.defer(() -> Flux.fromIterable(pedidoRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    public Mono<PedidoDTO> buscarPorId(Long id) {
        return Mono.fromCallable(() -> pedidoRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    @Transactional
    public Mono<PedidoDTO> crear(PedidoDTO pedidoDTO) {
        return Mono.fromCallable(() -> {
                    validarPedido(pedidoDTO);

                    // Validar stock y obtener precios
                    for (DetallePedidoDTO detalle : pedidoDTO.getDetalles()) {
                        ProductoDTO producto = productoClient.obtenerProducto(detalle.getProductoId());

                        if (producto == null) {
                            throw new BadRequestException("Producto no encontrado: " + detalle.getProductoId());
                        }

                        if (!producto.getActivo()) {
                            throw new BadRequestException("Producto inactivo: " + producto.getNombre());
                        }

                        if (producto.getStock() < detalle.getCantidad()) {
                            throw new BadRequestException("Stock insuficiente para: " + producto.getNombre());
                        }

                        detalle.setPrecioUnitario(producto.getPrecio());
                    }

                    // Calcular total
                    double total = pedidoDTO.getDetalles().stream()
                            .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                            .sum();

                    Pedido pedido = convertToEntity(pedidoDTO);
                    pedido.setTotal(total);
                    pedido.setEstado("PENDIENTE");

                    Pedido pedidoGuardado = pedidoRepository.save(pedido);

                    // Actualizar stock en ms-productos
                    for (DetallePedidoDTO detalle : pedidoDTO.getDetalles()) {
                        productoClient.actualizarStock(detalle.getProductoId(), detalle.getCantidad());
                    }

                    return pedidoGuardado;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    @Transactional
    public Mono<PedidoDTO> actualizarEstado(Long id, String nuevoEstado) {
        return Mono.fromCallable(() -> {
                    Pedido pedido = pedidoRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

                    if (!List.of("PENDIENTE", "PROCESADO", "CANCELADO").contains(nuevoEstado)) {
                        throw new BadRequestException("Estado inválido");
                    }

                    pedido.setEstado(nuevoEstado);
                    return pedidoRepository.save(pedido);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    @Transactional
    public Mono<Void> eliminar(Long id) {
        return Mono.fromRunnable(() -> {
                    if (!pedidoRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Pedido no encontrado con id: " + id);
                    }
                    pedidoRepository.deleteById(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void validarPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getCliente() == null || pedidoDTO.getCliente().trim().isEmpty()) {
            throw new BadRequestException("El cliente es obligatorio");
        }
        if (pedidoDTO.getDetalles() == null || pedidoDTO.getDetalles().isEmpty()) {
            throw new BadRequestException("El pedido debe tener al menos un producto");
        }
    }

    private PedidoDTO convertToDTO(Pedido pedido) {
        List<DetallePedidoDTO> detallesDTO = pedido.getDetalles().stream()
                .map(d -> new DetallePedidoDTO(d.getId(), d.getProductoId(), d.getCantidad(), d.getPrecioUnitario()))
                .collect(Collectors.toList());

        return new PedidoDTO(
                pedido.getId(),
                pedido.getCliente(),
                pedido.getFecha(),
                pedido.getTotal(),
                pedido.getEstado(),
                detallesDTO
        );
    }

    private Pedido convertToEntity(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setCliente(dto.getCliente());

        dto.getDetalles().forEach(detalleDTO -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(detalleDTO.getProductoId());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            pedido.addDetalle(detalle);
        });

        return pedido;
    }
}
