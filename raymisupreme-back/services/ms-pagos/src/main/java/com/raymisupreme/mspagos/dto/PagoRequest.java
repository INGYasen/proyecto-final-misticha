package com.raymisupreme.mspagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequest {

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long clienteId;

    @NotNull
    @Positive
    private BigDecimal monto;

    @NotBlank
    private String metodoPago;
}
