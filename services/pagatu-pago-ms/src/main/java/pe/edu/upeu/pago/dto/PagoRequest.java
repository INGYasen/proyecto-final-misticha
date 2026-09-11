package pe.edu.upeu.pago.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PagoRequest {

    @NotBlank
    @Size(max = 120)
    private String codigo;

    @NotNull
    private Long ordenId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal monto;

    @NotBlank
    @Size(max = 120)
    private String estado;

    @NotBlank
    @Size(max = 120)
    private String metodo;

}
