package com.raymisupreme.mscatalogo.dto;

import lombok.Data;

@Data
public class ItemCatalogoResponse {

    private Long idItem;
    private Long idPrenda;
    private String urlFoto;
    private String descripcionPublica;
    private Boolean destacado;
    private Boolean activo;
}
