package com.raymisupreme.mspagos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PagoEstadoRequest {

    @NotBlank
    private String estado;
}
