package com.practica.productos.event;

import com.practica.productos.dto.ProductoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductoUpdatedEvent extends ProductoEvent {
    private Long productoId;
    private ProductoDTO payload;

    public ProductoUpdatedEvent(Long productoId, ProductoDTO producto) {
        super("PRODUCTO_UPDATED");
        this.productoId = productoId;
        this.payload = producto;
    }
}
