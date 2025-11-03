package com.practica.pedidos.controller;

import com.practica.pedidos.dto.PedidoDTO;
import com.practica.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Pedidos", description = "API para gestión de pedidos")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Listar todos los pedidos", description = "Obtiene una lista de todos los pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    })
    @GetMapping
    public Flux<PedidoDTO> getAll() {
        return pedidoService.listarTodos();
    }

    @Operation(summary = "Obtener pedido por ID", description = "Busca un pedido específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PedidoDTO>> getById(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Crear nuevo pedido", description = "Crea un nuevo pedido en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public Mono<ResponseEntity<PedidoDTO>> create(@RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.crear(pedidoDTO)
                .map(pedido -> ResponseEntity.status(HttpStatus.CREATED).body(pedido));
    }

    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado de un pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<PedidoDTO>> updateStatus(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Parameter(description = "Nuevo estado (PENDIENTE, PROCESADO, CANCELADO)") @RequestParam String estado) {
        return pedidoService.actualizarEstado(id, estado)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return pedidoService.eliminar(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
