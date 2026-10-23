package com.raymisupreme.mspagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MercadoPagoPreferenciaRequest {
    @NotNull
    private Long pedidoId;
}
