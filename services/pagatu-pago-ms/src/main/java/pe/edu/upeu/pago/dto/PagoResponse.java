package pe.edu.upeu.pago.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    private Long id;
    private String codigo;
    private Long ordenId;
    private BigDecimal monto;
    private String estado;
    private String metodo;
}
