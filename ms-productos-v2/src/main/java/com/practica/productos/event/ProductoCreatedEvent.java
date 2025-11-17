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
public class ProductoCreatedEvent extends ProductoEvent {
    private ProductoDTO payload;

    public ProductoCreatedEvent(ProductoDTO producto) {
        super("PRODUCTO_CREATED");
        this.payload = producto;
    }
}
