package pe.edu.upeu.orden.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrdenDetalleRequest {

    @NotNull
    private Long ordenId;

    @NotNull
    private Long idProducto;

    @Size(max = 150)
    private String descripcion;

    @NotNull
    @Min(value = 1, message = "debe ser al menos 1")
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precioUnitario;
}
