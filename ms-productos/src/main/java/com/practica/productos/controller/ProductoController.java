package com.practica.productos.controller;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public Flux<ProductoDTO> listarTodos(@RequestParam(required = false, defaultValue = "false") Boolean soloActivos) {
        return soloActivos ? productoService.listarActivos() : productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductoDTO>> buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductoDTO>> crear(@RequestBody ProductoDTO productoDTO) {
        return productoService.crear(productoDTO)
                .map(producto -> ResponseEntity.status(HttpStatus.CREATED).body(producto));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductoDTO>> actualizar(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        return productoService.actualizar(id, productoDTO)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable Long id) {
        return productoService.eliminar(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @PutMapping("/{id}/stock")
    public Mono<ResponseEntity<Void>> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        return productoService.actualizarStock(id, cantidad)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @GetMapping("/bajo-stock")
    public Flux<ProductoDTO> obtenerProductosBajoStock(@RequestParam(defaultValue = "10") Integer minimo) {
        return productoService.obtenerProductosBajoStock(minimo);
    }
}
