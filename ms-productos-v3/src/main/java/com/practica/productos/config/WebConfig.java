package com.practica.productos.config;

import com.practica.productos.converter.ProductoCreateRequestToModelConverter;
import com.practica.productos.converter.ProductoModelToProductoDtoConverter;
import com.practica.productos.converter.ProductoUpdateRequestToModelConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {

    private final ProductoModelToProductoDtoConverter toDtoConverter;
    private final ProductoCreateRequestToModelConverter createConverter;
    private final ProductoUpdateRequestToModelConverter updateConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(toDtoConverter);
        registry.addConverter(createConverter);
        registry.addConverter(updateConverter);
    }
}
