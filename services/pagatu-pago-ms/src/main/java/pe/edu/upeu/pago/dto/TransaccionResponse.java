package pe.edu.upeu.pago.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionResponse {
    private Long id;
    private Long pagoId;
    private String referencia;
    private String canal;
    private String estado;
}
