package pe.edu.upeu.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoRequest {

    @NotNull
    private Long stockId;

    @NotBlank
    @Size(max = 20)
    private String tipo;

    @NotNull
    @Min(1)
    private Integer cantidad;

    @Size(max = 255)
    private String motivo;
}
