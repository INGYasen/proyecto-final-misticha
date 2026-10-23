package com.raymisupreme.mspagos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ComprobanteResponse {

    private Long id;
    private Long pagoId;
    private String numeroComprobante;
    private String tipo;
    private LocalDateTime fechaEmision;
    private BigDecimal montoTotal;
    private Boolean activo;
}
