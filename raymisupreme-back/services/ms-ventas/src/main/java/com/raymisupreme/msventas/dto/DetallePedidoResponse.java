package com.raymisupreme.msventas.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponse {
    private Long idDetalle;
    private Long idPedido;
    private Long idPrenda;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}

