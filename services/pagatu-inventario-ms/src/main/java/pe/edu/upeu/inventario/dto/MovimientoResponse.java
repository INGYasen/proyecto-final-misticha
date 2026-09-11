package pe.edu.upeu.inventario.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {
    private Long id;
    private Long stockId;
    private String tipo;
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fecha;
}
