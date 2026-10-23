package com.raymisupreme.mscatalogo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCatalogoResponse {

    private Long idPrenda;
    private String nombre;
    private PrendaInventarioResponse prenda;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precioVenta;
    private BigDecimal descuento;
    private BigDecimal precioFinal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mensajeInventario;
}
