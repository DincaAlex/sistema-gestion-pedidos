package com.practica.productos.repository;

import com.practica.productos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    @Modifying
    @Query(value = "CALL actualizar_stock(:productoId, :cantidad)", nativeQuery = true)
    void actualizarStock(@Param("productoId") Long productoId, @Param("cantidad") Integer cantidad);

    @Query(value = "SELECT * FROM productos_bajo_stock(:minimo)", nativeQuery = true)
    List<Producto> obtenerProductosBajoStock(@Param("minimo") Integer minimo);
}
