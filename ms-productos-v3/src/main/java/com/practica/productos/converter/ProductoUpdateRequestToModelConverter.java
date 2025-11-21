package com.practica.productos.converter;

import com.practica.productos.adapter.rest.model.ProductoUpdateRequest;
import com.practica.productos.model.ProductoModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductoUpdateRequestToModelConverter implements Converter<ProductoUpdateRequest, ProductoModel> {

    @Override
    public ProductoModel convert(ProductoUpdateRequest source) {
        return ProductoModel.builder()
                .nombre(source.getNombre())
                .descripcion(source.getDescripcion())
                .precio(source.getPrecio().doubleValue())
                .stock(source.getStock())
                .activo(source.getActivo() != null ? source.getActivo() : true)
                .build();
    }
}
