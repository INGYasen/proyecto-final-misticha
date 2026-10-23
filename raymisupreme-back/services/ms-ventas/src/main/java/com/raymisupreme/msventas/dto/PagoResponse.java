package com.raymisupreme.msventas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoResponse {

    private Long id;
    private Long pedidoId;
    private Long clienteId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String referenciaPago;
    private LocalDateTime fechaPago;
    private String numeroComprobante;
}
