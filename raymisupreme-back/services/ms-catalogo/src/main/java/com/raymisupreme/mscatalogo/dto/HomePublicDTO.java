package com.raymisupreme.mscatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomePublicDTO {

    private String bannerPrincipal;
    private String titulo;
    private String subtitulo;
    private String textoCampania;
    private List<CategoriaPublicDTO> categoriasDestacadas;
    private List<ProductoPublicDTO> productosDestacados;
    private List<PromocionPublicDTO> promocionesActivas;
}
