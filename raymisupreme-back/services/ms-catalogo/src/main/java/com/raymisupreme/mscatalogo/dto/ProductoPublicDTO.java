package com.raymisupreme.mscatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPublicDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioBase;
    private String imagen;
    private CategoriaPublicDTO categoria;
    private String publico;
    private String talla;
    private String color;
    private String material;
    private Integer stockDisponible;
    private Boolean disponible;
    private Boolean destacado;
}
