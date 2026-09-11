package pe.edu.upeu.notificacion.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CanalRequest {

    @NotBlank
    @Size(max = 120)
    private String codigo;

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotNull
    private Boolean activo;

}
