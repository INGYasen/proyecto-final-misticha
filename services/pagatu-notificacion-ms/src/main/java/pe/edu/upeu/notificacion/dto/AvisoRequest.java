package pe.edu.upeu.notificacion.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvisoRequest {

    @NotNull
    private Long canalId;

    @NotBlank
    @Size(max = 120)
    private String destinatario;

    @NotBlank
    @Size(max = 120)
    private String asunto;

    @NotBlank
    @Size(max = 500)
    private String mensaje;

    @NotBlank
    @Size(max = 120)
    private String estado;

}
