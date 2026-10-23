package com.raymisupreme.mscarrito.client.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductoCatalogoResponse {

    private Long idPrenda;
    private String nombre;
    private PrendaCatalogoResponse prenda;
    private BigDecimal precioFinal;
    private String mensajeInventario;
}
