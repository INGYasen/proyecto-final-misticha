package pe.edu.upeu.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockRequest {

    @NotNull
    private Long idProducto;

    @NotBlank
    @Size(max = 40)
    private String sku;

    @NotNull
    @Min(0)
    private Integer cantidad;

    @NotNull
    @Min(0)
    private Integer minimo;

    @NotBlank
    @Size(max = 80)
    private String almacen;
}
