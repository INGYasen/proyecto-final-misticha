package com.raymisupreme.mscatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrendaInventarioResponse {

    private Long idPrenda;
    private Long idLote;
    private Long idCategoria;
    private String nombreCategoria;
    private String publico;
    private String codigoUnico;
    private String nombrePrenda;
    private String talla;
    private String color;
    private String material;
    private Integer stock;
    private String urlFoto;
}
