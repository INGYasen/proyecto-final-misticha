package com.raymisupreme.msventas.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequest {

    private Long pedidoId;
    private Long clienteId;
    private BigDecimal monto;
    private String metodoPago;
}
