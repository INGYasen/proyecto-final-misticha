package pe.edu.upeu.orden.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdenRequest {

    @NotBlank
    @Size(max = 30)
    private String codigo;

    @NotBlank
    @Size(max = 120)
    private String cliente;

    @NotBlank
    @Pattern(regexp = "CREADA|PAGADA|ANULADA", message = "debe ser CREADA, PAGADA o ANULADA")
    private String estado;
}
