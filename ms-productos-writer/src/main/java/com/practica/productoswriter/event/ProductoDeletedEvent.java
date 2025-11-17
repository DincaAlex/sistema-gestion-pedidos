package com.practica.productoswriter.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProductoDeletedEvent extends ProductoEvent {
    private Long productoId;

    public ProductoDeletedEvent(Long productoId) {
        super("PRODUCTO_DELETED");
        this.productoId = productoId;
    }
}
