package pe.edu.upeu.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {

    @NotBlank
    @Size(max = 120)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotNull
    private Boolean activo;

    @NotNull
    private Long rolId;

}
