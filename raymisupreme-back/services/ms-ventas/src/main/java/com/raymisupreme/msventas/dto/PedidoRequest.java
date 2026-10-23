package com.raymisupreme.msventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {
    private Long idCliente;
    private LocalDateTime fechaPedido;
    private String estadoPedido;
    private BigDecimal totalPagar;
}

