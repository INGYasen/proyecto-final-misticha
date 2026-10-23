package com.raymisupreme.msinventario.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrendaResponse {
    private Long idPrenda;
    private Long idLote;
    private Long idCategoria;
    private String nombreCategoria;
    private PublicoObjetivo publico;
    private String codigoUnico;
    private String nombrePrenda;
    private String talla;
    private String color;
    private String material;
    private Integer stock;
    private String urlFoto;
}
