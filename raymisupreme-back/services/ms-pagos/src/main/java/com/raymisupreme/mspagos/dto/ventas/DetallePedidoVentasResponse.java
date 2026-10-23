package com.raymisupreme.mspagos.dto.ventas;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetallePedidoVentasResponse {
    private Long idDetalle;
    private Long idPedido;
    private Long idPrenda;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
