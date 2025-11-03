package com.practica.pedidos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFound(
            ResourceNotFoundException ex, ServerWebExchange exchange) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "No Encontrado",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return Mono.just(new ResponseEntity<>(error, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(
            BadRequestException ex, ServerWebExchange exchange) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Solicitud Incorrecta",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return Mono.just(new ResponseEntity<>(error, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebClientResponseException(
            WebClientResponseException ex, ServerWebExchange exchange) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                "Error de Servicio Externo",
                "Error comunicándose con servicio externo: " + ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return Mono.just(new ResponseEntity<>(error, ex.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGlobalException(
            Exception ex, ServerWebExchange exchange) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return Mono.just(new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
