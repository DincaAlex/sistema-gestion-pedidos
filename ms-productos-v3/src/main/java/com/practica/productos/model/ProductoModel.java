package com.practica.productos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("productos")
public class ProductoModel {

    @Id
    private Long id;

    private String nombre;

    private String descripcion;

    private Double precio;

    private Integer stock;

    private Boolean activo;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}
