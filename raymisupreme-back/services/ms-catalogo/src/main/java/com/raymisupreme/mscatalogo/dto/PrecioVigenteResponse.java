package com.raymisupreme.mscatalogo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de compatibilidad para respuestas de precio vigente.
 */
@Data
public class PrecioVigenteResponse {

    @JsonProperty("prendaId")
    private Long idPrenda;

    @JsonProperty("precioFinal")
    private BigDecimal precioVigente;

    private String moneda;
}
