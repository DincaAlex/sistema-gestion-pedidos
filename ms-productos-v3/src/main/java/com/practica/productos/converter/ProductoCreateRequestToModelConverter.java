package com.practica.productos.converter;

import com.practica.productos.adapter.rest.model.ProductoCreateRequest;
import com.practica.productos.model.ProductoModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductoCreateRequestToModelConverter implements Converter<ProductoCreateRequest, ProductoModel> {

    @Override
    public ProductoModel convert(ProductoCreateRequest source) {
        return ProductoModel.builder()
                .nombre(source.getNombre())
                .descripcion(source.getDescripcion())
                .precio(source.getPrecio().doubleValue())
                .stock(source.getStock())
                .activo(source.getActivo() != null ? source.getActivo() : true)
                .build();
    }
}
