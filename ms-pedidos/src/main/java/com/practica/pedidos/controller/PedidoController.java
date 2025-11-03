package com.practica.pedidos.controller;

import com.practica.pedidos.dto.PedidoDTO;
import com.practica.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public Flux<PedidoDTO> getAll() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PedidoDTO>> getById(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<PedidoDTO>> create(@RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.crear(pedidoDTO)
                .map(pedido -> ResponseEntity.status(HttpStatus.CREATED).body(pedido));
    }

    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<PedidoDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam String estado) {
        return pedidoService.actualizarEstado(id, estado)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return pedidoService.eliminar(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
