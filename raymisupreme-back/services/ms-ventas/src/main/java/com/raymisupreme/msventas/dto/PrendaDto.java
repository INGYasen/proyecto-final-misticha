package com.raymisupreme.msventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa los datos de una prenda recibidos desde ms-inventario via Feign.
 * Los campos deben coincidir con PrendaResponse de ms-inventario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrendaDto {
    private Long idPrenda;
    private Long idLote;
    private Long idCategoria;
    private String codigoUnico;
    private String talla;
    private String color;
    private String material;
    private Integer stock;
}
