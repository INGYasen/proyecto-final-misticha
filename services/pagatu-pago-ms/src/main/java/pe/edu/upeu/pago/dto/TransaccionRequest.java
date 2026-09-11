package pe.edu.upeu.pago.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransaccionRequest {

    @NotNull
    private Long pagoId;

    @NotBlank
    @Size(max = 120)
    private String referencia;

    @NotBlank
    @Size(max = 120)
    private String canal;

    @NotBlank
    @Size(max = 120)
    private String estado;

}
