package pe.edu.upeu.inventario.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private Long id;
    private Long idProducto;
    private String sku;
    private Integer cantidad;
    private Integer minimo;
    private String almacen;
}
