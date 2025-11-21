package com.practica.productos.handler;

import com.practica.productos.adapter.rest.model.ErrorDetail;
import com.practica.productos.adapter.rest.model.ErrorModel;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorModel> handleResourceNotFound(
            ResourceNotFoundException ex,
            ServerWebExchange exchange) {

        log.error("Resource not found: {}", ex.getMessage());

        ErrorDetail detail = new ErrorDetail();
        detail.setCode("E404_NOT_FOUND");
        detail.setMessage(ex.getMessage());
        detail.setField("id");

        ErrorModel error = new ErrorModel();
        error.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        error.setStatus(404);
        error.setError("No Encontrado");
        error.setMessage(ex.getMessage());
        error.setPath(exchange.getRequest().getPath().value());
        error.setErrors(Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorModel> handleBadRequest(
            BadRequestException ex,
            ServerWebExchange exchange) {

        log.error("Bad request: {}", ex.getMessage());

        ErrorDetail detail = new ErrorDetail();
        detail.setCode("E400_VALIDATION");
        detail.setMessage(ex.getMessage());

        ErrorModel error = new ErrorModel();
        error.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        error.setStatus(400);
        error.setError("Solicitud Incorrecta");
        error.setMessage(ex.getMessage());
        error.setPath(exchange.getRequest().getPath().value());
        error.setErrors(Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorModel> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Internal server error", ex);

        ErrorDetail detail = new ErrorDetail();
        detail.setCode("E500_INTERNAL_ERROR");
        detail.setMessage("Error interno del servidor");

        ErrorModel error = new ErrorModel();
        error.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        error.setStatus(500);
        error.setError("Error Interno del Servidor");
        error.setMessage("Ha ocurrido un error inesperado");
        error.setPath(exchange.getRequest().getPath().value());
        error.setErrors(Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
