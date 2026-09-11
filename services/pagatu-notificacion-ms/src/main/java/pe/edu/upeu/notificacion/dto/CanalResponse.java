package pe.edu.upeu.notificacion.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanalResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private Boolean activo;
}
