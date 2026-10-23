package com.raymisupreme.mscatalogo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogoPrendaResponse {

    private Long idPrenda;
    private Long idCategoria;
    private String nombreCategoria;
    private String publico;
    private String codigoUnico;
    private String nombrePrenda;
    private String talla;
    private String color;
    private String material;
    private String urlFoto;
    private String descripcionPublica;
    private Boolean destacado;
    private BigDecimal precioVigente;
    private Integer stockDisponible;
    private Boolean disponibleParaVenta;
}
