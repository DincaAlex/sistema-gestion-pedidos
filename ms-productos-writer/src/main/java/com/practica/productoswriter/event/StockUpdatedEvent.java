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
public class StockUpdatedEvent extends ProductoEvent {
    private Long productoId;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;

    public StockUpdatedEvent(Long productoId, Integer cantidad, Integer stockAnterior, Integer stockNuevo) {
        super("STOCK_UPDATED");
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
    }
}
