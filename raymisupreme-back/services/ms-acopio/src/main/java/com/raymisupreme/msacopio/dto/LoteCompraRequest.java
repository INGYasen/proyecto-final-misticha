package com.raymisupreme.msacopio.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoteCompraRequest {
    // Solo pedimos el ID del artesano, el sistema se encarga de buscar quién es
    private Long idArtesano;
    private LocalDate fechaCompra;
    private BigDecimal precioTotalPagado;
    private Integer cantidadTotalRecibida;
    private String descripcionGeneral;
    private String estadoLote;
}