package com.practica.pedidos.client;

import com.practica.pedidos.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-productos", url = "${ms-productos.url}")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable Long id);

    @PutMapping("/api/productos/{id}/stock")
    void actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad);
}
