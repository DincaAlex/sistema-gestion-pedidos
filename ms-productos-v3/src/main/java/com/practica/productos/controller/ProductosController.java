package com.practica.productos.controller;

import com.practica.productos.adapter.rest.api.ProductosApi;
import com.practica.productos.adapter.rest.model.*;
import com.practica.productos.converter.ProductoCreateRequestToModelConverter;
import com.practica.productos.converter.ProductoModelToProductoDtoConverter;
import com.practica.productos.converter.ProductoUpdateRequestToModelConverter;
import com.practica.productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v3")
@RequiredArgsConstructor
public class ProductosController implements ProductosApi {

    private final ProductoService productoService;
    private final ProductoModelToProductoDtoConverter toDtoConverter;
    private final ProductoCreateRequestToModelConverter createConverter;
    private final ProductoUpdateRequestToModelConverter updateConverter;

    @Override
    public Mono<ResponseEntity<SuccessListResponse>> listProductos(
            Boolean onlyActive,
            ServerWebExchange exchange) {

        return productoService.listar(onlyActive)
                .map(toDtoConverter::convert)
                .collectList()
                .map(productos -> {
                    SuccessListResponse response = new SuccessListResponse();
                    response.setSuccess(true);
                    response.setMessage("Productos obtenidos exitosamente");
                    response.setData(productos);

                    PaginationMeta meta = new PaginationMeta();
                    meta.setTotal(productos.size());
                    meta.setPage(1);
                    meta.setLimit(productos.size());
                    response.setMeta(meta);

                    return ResponseEntity.ok(response);
                });
    }

    @Override
    public Mono<ResponseEntity<SuccessObjectResponse>> getProductoById(
            Long id,
            ServerWebExchange exchange) {

        return productoService.obtenerPorId(id)
                .map(toDtoConverter::convert)
                .map(producto -> {
                    SuccessObjectResponse response = new SuccessObjectResponse();
                    response.setSuccess(true);
                    response.setMessage("Producto encontrado");
                    response.setData(producto);

                    return ResponseEntity.ok(response);
                });
    }

    @Override
    public Mono<ResponseEntity<SuccessObjectResponse>> createProducto(
            Mono<ProductoCreateRequest> productoCreateRequest,
            ServerWebExchange exchange) {

        return productoCreateRequest
                .map(createConverter::convert)
                .flatMap(productoService::crear)
                .map(producto -> {
                    Producto dto = toDtoConverter.convert(producto);

                    SuccessObjectResponse response = new SuccessObjectResponse();
                    response.setSuccess(true);
                    response.setMessage("Producto creado y enviado para procesamiento");
                    response.setData(dto);

                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                });
    }

    @Override
    public Mono<ResponseEntity<SuccessObjectResponse>> updateProducto(
            Long id,
            Mono<ProductoUpdateRequest> productoUpdateRequest,
            ServerWebExchange exchange) {

        return productoUpdateRequest
                .map(updateConverter::convert)
                .flatMap(producto -> productoService.actualizar(id, producto))
                .map(producto -> {
                    Producto dto = toDtoConverter.convert(producto);

                    SuccessObjectResponse response = new SuccessObjectResponse();
                    response.setSuccess(true);
                    response.setMessage("Producto actualizado y enviado para procesamiento");
                    response.setData(dto);

                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                });
    }

    @Override
    public Mono<ResponseEntity<DeleteProducto202Response>> deleteProducto(
            Long id,
            ServerWebExchange exchange) {

        return productoService.eliminar(id)
                .then(Mono.fromCallable(() -> {
                    DeleteProducto202Response response = new DeleteProducto202Response();
                    response.setSuccess(true);
                    response.setMessage("Producto marcado para eliminación");

                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                }));
    }

    @Override
    public Mono<ResponseEntity<UpdateStock202Response>> updateStock(
            Long id,
            Integer cantidad,
            ServerWebExchange exchange) {

        return productoService.actualizarStock(id, cantidad)
                .then(Mono.fromCallable(() -> {
                    UpdateStock202Response response = new UpdateStock202Response();
                    response.setSuccess(true);
                    response.setMessage("Stock actualizado correctamente");

                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                }));
    }

    @Override
    public Mono<ResponseEntity<GetLowStockProductos200Response>> getLowStockProductos(
            Integer minimo,
            ServerWebExchange exchange) {

        return productoService.obtenerProductosBajoStock(minimo)
                .map(toDtoConverter::convert)
                .collectList()
                .map(productos -> {
                    GetLowStockProductos200Response response = new GetLowStockProductos200Response();
                    response.setSuccess(true);
                    response.setMessage("Productos con stock bajo obtenidos");
                    response.setData(productos);

                    return ResponseEntity.ok(response);
                });
    }
}
