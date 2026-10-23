package com.raymisupreme.mspagos.dto;

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
    private String statusDetail;
    private String referenciaPago;
    private LocalDateTime fechaPago;
    private String numeroComprobante;
}
