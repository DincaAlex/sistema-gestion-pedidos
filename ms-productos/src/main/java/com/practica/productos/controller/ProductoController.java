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
    public Flux<ProductoDTO> listAll(@RequestParam(required = false, defaultValue = "false") Boolean onlyActive) {
        return onlyActive ? productoService.getActive() : productoService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductoDTO>> getById(@PathVariable Long id) {
        return productoService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductoDTO> create(@RequestBody ProductoDTO productoDTO) {
        return productoService.create(productoDTO);
    }

    @PutMapping("/{id}")
    public Mono<ProductoDTO> update(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        return productoService.update(id, productoDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return productoService.delete(id);
    }

    @PutMapping("/{id}/stock")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> updateStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        return productoService.updateStock(id, cantidad);
    }

    @GetMapping("/low-stock")
    public Flux<ProductoDTO> getLowStock(@RequestParam(defaultValue = "10") Integer minimo) {
        return productoService.getProductsLowStock(minimo);
    }
}