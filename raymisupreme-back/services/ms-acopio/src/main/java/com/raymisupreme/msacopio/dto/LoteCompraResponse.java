package com.raymisupreme.msacopio.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoteCompraResponse {
    private Long idLote;
    private Long idArtesano;

    // Le agregamos el nombre completo para que el Frontend no tenga que buscarlo
    private String nombreArtesano;

    private LocalDate fechaCompra;
    private BigDecimal precioTotalPagado;
    private Integer cantidadTotalRecibida;
    private String descripcionGeneral;
    private String estadoLote;
}