package com.practica.productos.controller;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.service.ProductoService;
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

@Tag(name = "Productos", description = "API para gestión de productos")
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar productos", description = "Obtiene todos los productos o solo los activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    @GetMapping
    public Flux<ProductoDTO> listAll(
            @Parameter(description = "Mostrar solo productos activos")
            @RequestParam(required = false, defaultValue = "false") Boolean onlyActive) {
        return onlyActive ? productoService.getActive() : productoService.getAll();
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca un producto específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductoDTO>> getById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return productoService.getById(id)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductoDTO> create(@RequestBody ProductoDTO productoDTO) {
        return productoService.create(productoDTO);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public Mono<ProductoDTO> update(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @RequestBody ProductoDTO productoDTO) {
        return productoService.update(id, productoDTO);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return productoService.delete(id);
    }

    @Operation(summary = "Actualizar stock", description = "Incrementa o decrementa el stock de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}/stock")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> updateStock(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Parameter(description = "Cantidad a incrementar (positivo) o decrementar (negativo)")
            @RequestParam Integer cantidad) {
        return productoService.updateStock(id, cantidad);
    }

    @Operation(summary = "Productos con stock bajo", description = "Obtiene productos con stock menor al mínimo especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @GetMapping("/low-stock")
    public Flux<ProductoDTO> getLowStock(
            @Parameter(description = "Stock mínimo")
            @RequestParam(defaultValue = "10") Integer minimo) {
        return productoService.getProductsLowStock(minimo);
    }
}