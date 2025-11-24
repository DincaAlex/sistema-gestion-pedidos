package com.practica.productos.service;

import com.practica.productos.dto.ProductoDTO;
import com.practica.productos.entity.Producto;
import com.practica.productos.exception.BadRequestException;
import com.practica.productos.exception.ResourceNotFoundException;
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
        producto1.setDescripcion("Laptop HP");
        producto1.setPrecio(1000.0);
        producto1.setStock(10);
        producto1.setActivo(true);
        producto1.setFechaCreacion(LocalDateTime.now());

        producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Mouse");
        producto2.setDescripcion("Mouse Logitech");
        producto2.setPrecio(20.0);
        producto2.setStock(50);
        producto2.setActivo(true);
        producto2.setFechaCreacion(LocalDateTime.now());

        productoDTO1 = new ProductoDTO(null, "Teclado", "Teclado Mecanico", 150.0, 20, true, null);
    }

    @Test
    void testGetAll_ShouldReturnAllProducts() {
        when(productoRepository.findAll()).thenReturn(Flux.just(producto1, producto2));

        StepVerifier.create(productoService.getAll())
                .expectNextMatches(dto -> dto.getNombre().equals("Laptop"))
                .expectNextMatches(dto -> dto.getNombre().equals("Mouse"))
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

        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void testCreate_WithValidData_ShouldCreateProduct() {
        Producto savedProducto = new Producto();
        savedProducto.setId(3L);
        savedProducto.setNombre("Teclado");
        savedProducto.setDescripcion("Teclado Mecanico");
        savedProducto.setPrecio(150.0);
        savedProducto.setStock(20);
        savedProducto.setActivo(true);
        savedProducto.setFechaCreacion(LocalDateTime.now());

        when(productoRepository.save(any(Producto.class))).thenReturn(Mono.just(savedProducto));

        StepVerifier.create(productoService.create(productoDTO1))
                .expectNextMatches(dto -> dto.getId().equals(3L) && dto.getNombre().equals("Teclado"))
                .verifyComplete();

        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testCreate_WithEmptyName_ShouldThrowException() {
        ProductoDTO invalidDTO = new ProductoDTO(null, "", "Descripcion", 100.0, 10, true, null);

        StepVerifier.create(productoService.create(invalidDTO))
                .expectError(BadRequestException.class)
                .verify();

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testCreate_WithNullName_ShouldThrowException() {
        ProductoDTO invalidDTO = new ProductoDTO(null, null, "Descripcion", 100.0, 10, true, null);

        StepVerifier.create(productoService.create(invalidDTO))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testCreate_WithInvalidPrice_ShouldThrowException() {
        ProductoDTO invalidDTO = new ProductoDTO(null, "Producto", "Descripcion", 0.0, 10, true, null);

        StepVerifier.create(productoService.create(invalidDTO))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testCreate_WithNegativeStock_ShouldThrowException() {
        ProductoDTO invalidDTO = new ProductoDTO(null, "Producto", "Descripcion", 100.0, -5, true, null);

        StepVerifier.create(productoService.create(invalidDTO))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testUpdate_WithValidData_ShouldUpdateProduct() {
        ProductoDTO updateDTO = new ProductoDTO(1L, "Laptop Updated", "Description", 1200.0, 15, true, null);

        when(productoRepository.findById(1L)).thenReturn(Mono.just(producto1));
        when(productoRepository.save(any(Producto.class))).thenReturn(Mono.just(producto1));

        StepVerifier.create(productoService.update(1L, updateDTO))
                .expectNextMatches(dto -> dto.getNombre().equals("Laptop Updated"))
                .verifyComplete();

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testUpdate_WhenProductNotExists_ShouldThrowException() {
        ProductoDTO updateDTO = new ProductoDTO(99L, "Producto", "Description", 100.0, 10, true, null);

        when(productoRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productoService.update(99L, updateDTO))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testDelete_WhenProductExists_ShouldDeleteProduct() {
        when(productoRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(productoRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productoService.delete(1L))
                .verifyComplete();

        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_WhenProductNotExists_ShouldThrowException() {
        when(productoRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(productoService.delete(99L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(productoRepository, times(1)).existsById(99L);
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateStock_WithValidQuantity_ShouldUpdateStock() {
        when(productoRepository.findById(1L)).thenReturn(Mono.just(producto1));
        when(productoRepository.actualizarStockConProcedimiento(eq(1L), anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(productoService.updateStock(1L, 5))
                .verifyComplete();

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).actualizarStockConProcedimiento(eq(1L), anyInt());
    }

    @Test
    void testUpdateStock_WithZeroQuantity_ShouldThrowException() {
        StepVerifier.create(productoService.updateStock(1L, 0))
                .expectError(BadRequestException.class)
                .verify();

        verify(productoRepository, never()).findById(anyLong());
    }

    @Test
    void testUpdateStock_WithInsufficientStock_ShouldThrowException() {
        when(productoRepository.findById(1L)).thenReturn(Mono.just(producto1));

        // Intentar decrementar más stock del disponible
        StepVerifier.create(productoService.updateStock(1L, -15))
                .expectError(BadRequestException.class)
                .verify();

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, never()).actualizarStockConProcedimiento(anyLong(), anyInt());
    }

    @Test
    void testGetProductsLowStock_ShouldReturnLowStockProducts() {
        when(productoRepository.obtenerProductosBajoStockConProcedimiento(10))
                .thenReturn(Flux.just(producto1));

        StepVerifier.create(productoService.getProductsLowStock(10))
                .expectNextMatches(dto -> dto.getId().equals(1L))
                .verifyComplete();

        verify(productoRepository, times(1)).obtenerProductosBajoStockConProcedimiento(10);
    }

    @Test
    void testGetProductsLowStock_WithNullMinimo_ShouldUseDefaultValue() {
        when(productoRepository.obtenerProductosBajoStockConProcedimiento(10))
                .thenReturn(Flux.just(producto1));

        StepVerifier.create(productoService.getProductsLowStock(null))
                .expectNextMatches(dto -> dto.getId().equals(1L))
                .verifyComplete();

        verify(productoRepository, times(1)).obtenerProductosBajoStockConProcedimiento(10);
    }
}
