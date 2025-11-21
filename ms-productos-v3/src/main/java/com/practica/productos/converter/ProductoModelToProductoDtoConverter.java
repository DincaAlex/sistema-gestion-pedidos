package com.practica.productos.converter;

import com.practica.productos.adapter.rest.model.Producto;
import com.practica.productos.model.ProductoModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class ProductoModelToProductoDtoConverter implements Converter<ProductoModel, Producto> {

    @Override
    public Producto convert(ProductoModel source) {
        Producto dto = new Producto();
        dto.setId(source.getId());
        dto.setNombre(source.getNombre());
        dto.setDescripcion(source.getDescripcion());
        dto.setPrecio(source.getPrecio());
        dto.setStock(source.getStock());
        dto.setActivo(source.getActivo());

        if (source.getFechaCreacion() != null) {
            dto.setFechaCreacion(OffsetDateTime.of(source.getFechaCreacion(), ZoneOffset.UTC));
        }

        return dto;
    }
}
