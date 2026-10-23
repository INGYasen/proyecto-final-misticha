package com.raymisupreme.msinventario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.raymisupreme.msinventario.client.dto.LoteCompraResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrendaDetalleCompletoResponse {

    private Long idPrenda;
    private String nombrePrenda;
    private String codigoUnico;
    private String talla;
    private String color;
    private String material;
    private Integer stock;
    private CategoriaResumenResponse categoria;
    private PublicoObjetivo publico;

    private LoteCompraResponse lote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mensaje;
}
//detalle completo de prenda, incluye información de lote de compra y categoría, además de un mensaje opcional para casos de error o información adicional.
