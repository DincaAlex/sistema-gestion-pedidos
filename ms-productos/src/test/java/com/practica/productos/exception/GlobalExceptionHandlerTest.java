package com.practica.productos.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private org.springframework.http.server.RequestPath requestPath;

    @Test
    void testHandleResourceNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Producto no encontrado");

        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/productos/999");

        StepVerifier.create(globalExceptionHandler.handleResourceNotFound(exception, exchange))
                .expectNextMatches(response ->
                    response.getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                    response.getBody() != null &&
                    response.getBody().getMessage().equals("Producto no encontrado")
                )
                .verifyComplete();
    }

    @Test
    void testHandleBadRequest() {
        BadRequestException exception = new BadRequestException("Datos inválidos");

        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/productos");

        StepVerifier.create(globalExceptionHandler.handleBadRequest(exception, exchange))
                .expectNextMatches(response ->
                    response.getStatusCode().equals(HttpStatus.BAD_REQUEST) &&
                    response.getBody() != null &&
                    response.getBody().getMessage().equals("Datos inválidos")
                )
                .verifyComplete();
    }

    @Test
    void testHandleGlobalException() {
        Exception exception = new RuntimeException("Error inesperado");

        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/productos");

        StepVerifier.create(globalExceptionHandler.handleGlobalException(exception, exchange))
                .expectNextMatches(response ->
                    response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR) &&
                    response.getBody() != null &&
                    response.getBody().getMessage().equals("Error inesperado")
                )
                .verifyComplete();
    }
}
