package com.raymisupreme.msinventario.dto;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrendaRequest {
    @NotNull
    private Long idLote;
    @NotNull
    private Long idCategoria;
    private PublicoObjetivo publico = PublicoObjetivo.UNISEX;

    @NotBlank
    @JsonAlias({"codigo_unico", "codigoSku", "codigo_sku"})
    private String codigoUnico;
    @NotBlank
    @JsonAlias("nombre_prenda")
    private String nombrePrenda;
    private String talla;
    private String color;
    private String material;
    private Integer stock;
    private String urlFoto;
}
