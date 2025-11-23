package com.practica.productos.handler;

import com.practica.productos.adapter.rest.model.ErrorDetail;
import com.practica.productos.adapter.rest.model.ErrorModel;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorModel> handleAuthenticationException(
            AuthenticationException ex,
            ServerWebExchange exchange) {

        log.error("Authentication error: {}", ex.getMessage());

        ErrorDetail detail = new ErrorDetail();
        detail.setCode("E401_UNAUTHORIZED");
        detail.setMessage("Autenticación requerida");

        ErrorModel error = new ErrorModel();
        error.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        error.setStatus(401);
        error.setError("No Autorizado");
        error.setMessage("Credenciales de autenticación no válidas o ausentes");
        error.setPath(exchange.getRequest().getPath().value());
        error.setErrors(Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorModel> handleAccessDeniedException(
            AccessDeniedException ex,
            ServerWebExchange exchange) {

        log.error("Access denied: {}", ex.getMessage());

        ErrorDetail detail = new ErrorDetail();
        detail.setCode("E403_FORBIDDEN");
        detail.setMessage("Acceso denegado");

        ErrorModel error = new ErrorModel();
        error.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        error.setStatus(403);
        error.setError("Prohibido");
        error.setMessage("No tiene permisos para acceder a este recurso");
        error.setPath(exchange.getRequest().getPath().value());
        error.setErrors(Collections.singletonList(detail));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
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
