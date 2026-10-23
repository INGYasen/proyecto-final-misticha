package com.raymisupreme.mspagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComprobanteRequest {

    @NotNull
    private Long pagoId;

    @NotBlank
    private String tipo;

    @NotNull
    @Positive
    private BigDecimal montoTotal;
}
