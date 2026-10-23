package com.raymisupreme.mscatalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCatalogoRequest {

    @NotNull
    private Long idPrenda;

    @NotBlank
    private String urlFoto;

    private String descripcionPublica;

    private Boolean destacado;

    private Boolean activo;
}
