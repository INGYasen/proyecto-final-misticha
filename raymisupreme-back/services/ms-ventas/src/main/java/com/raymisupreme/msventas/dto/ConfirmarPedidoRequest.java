package com.raymisupreme.msventas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmarPedidoRequest {

    @NotBlank
    private String metodoPago;
}
