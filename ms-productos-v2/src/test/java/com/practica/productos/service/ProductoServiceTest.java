package com.practica.productos.service;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.entity.Producto;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
import com.practica.productos.kafka.ProductoEventProducer;
import com.practica.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoEventProducer eventProducer;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto1;
    private Producto producto2;
    private ProductoDTO productoDTO1;

    @BeforeEach
    void setUp() {
        producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Laptop");
        producto1.setDescripcion("Laptop Dell");
        producto1.setPrecio(1500.0);
        producto1.setStock(15);
        producto1.setActivo(true);
        producto1.setFechaCreacion(LocalDateTime.now());

        producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Monitor");
        producto2.setDescripcion("Monitor LG");
        producto2.setPrecio(300.0);
        producto2.setStock(25);
        producto2.setActivo(true);
        producto2.setFechaCreacion(LocalDateTime.now());

        productoDTO1 = new ProductoDTO(null, "Teclado", "Teclado Gaming", 120.0, 30, true, null);
    }

    @Test
    void testGetAll_ShouldReturnAllProducts() {
        when(productoRepository.findAll()).thenReturn(Flux.just(producto1, producto2));

        StepVerifier.create(productoService.getAll())
                .expectNextMatches(dto -> dto.getNombre().equals("Laptop"))
                .expectNextMatches(dto -> dto.getNombre().equals("Monitor"))
                .verifyComplete();

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testGetActive_ShouldReturnActiveProducts() {
        when(productoRepository.findByActivoTrue()).thenReturn(Flux.just(producto1, producto2));

        StepVerifier.create(productoService.getActive())
                .expectNextMatches(dto -> dto.getActivo())
                .expectNextMatches(dto -> dto.getActivo())
                .verifyComplete();

        verify(productoRepository, times(1)).findByActivoTrue();
    }

    @Test
    void testGetById_WhenProductExists_ShouldReturnProduct() {
        when(productoRepository.findById(1L)).thenReturn(Mono.just(producto1));

        StepVerifier.create(productoService.getById(1L))
                .expectNextMatches(dto -> dto.getId().equals(1L) && dto.getNombre().equals("Laptop"))
                .verifyComplete();

        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_WhenProductNotExists_ShouldThrowException() {
        when(productoRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productoService.getById(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void testCreate_WithValidData_ShouldPublishEvent() {
        when(eventProducer.publishEvent(anyString(), any())).thenReturn(Mono.empty());

        StepVerifier.create(productoService.create(productoDTO1))
                .expectNextMatches(dto -> dto.getNombre().equals("Teclado"))
                .verifyComplete();

        verify(eventProducer, times(1)).publishEvent(anyString(), any());
    }

    @Test
    void testCreate_WithEmptyName_ShouldThrowException() {
        ProductoDTO invalidDTO = new ProductoDTO(null, "", "Descripcion", 100.0, 10, true, null);

        StepVerifier.create(productoService.create(invalidDTO))
                .expectError(BadRequestException.class)
                .verify();

        verify(eventProducer, never()).publishEvent(anyString(), any());
    }

    @Test
    void testCreate_WithInvalidPrice_ShouldThrowException() {
        ProductoDTO invalidDTO = new ProductoDTO(null, "Producto", "Descripcion", 0.0, 10, true, null);

        StepVerifier.create(productoService.create(invalidDTO))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testUpdate_WithValidData_ShouldPublishEvent() {
        ProductoDTO updateDTO = new ProductoDTO(1L, "Laptop Updated", "Description", 1600.0, 20, true, null);

        when(productoRepository.findById(1L)).thenReturn(Mono.just(producto1));
        when(eventProducer.publishEvent(anyLong(), any())).thenReturn(Mono.empty());

        StepVerifier.create(productoService.update(1L, updateDTO))
                .expectNextMatches(dto -> dto.getNombre().equals("Laptop Updated"))
                .verifyComplete();

        verify(productoRepository, times(1)).findById(1L);
        verify(eventProducer, times(1)).publishEvent(anyLong(), any());
    }

    @Test
    void testUpdate_WhenProductNotExists_ShouldThrowException() {
        ProductoDTO updateDTO = new ProductoDTO(99L, "Producto", "Description", 100.0, 10, true, null);

        when(productoRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productoService.update(99L, updateDTO))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(eventProducer, never()).publishEvent(anyLong(), any());
    }

    @Test
    void testDelete_WhenProductExists_ShouldPublishEvent() {
        when(productoRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(eventProducer.publishEvent(anyLong(), any())).thenReturn(Mono.empty());

        StepVerifier.create(productoService.delete(1L))
                .verifyComplete();

        verify(productoRepository, times(1)).existsById(1L);
        verify(eventProducer, times(1)).publishEvent(anyLong(), any());
    }

    @Test
    void testDelete_WhenProductNotExists_ShouldThrowException() {
        when(productoRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(productoService.delete(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(eventProducer, never()).publishEvent(anyLong(), any());
    }
}
