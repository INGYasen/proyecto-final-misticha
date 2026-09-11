package pe.edu.upeu.notificacion.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvisoResponse {
    private Long id;
    private Long canalId;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String estado;
}
