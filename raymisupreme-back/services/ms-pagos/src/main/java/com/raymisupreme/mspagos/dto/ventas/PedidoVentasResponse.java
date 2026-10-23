package com.raymisupreme.mspagos.dto.ventas;

import lombok.Data;

@Data
public class PedidoVentasResponse {
    private Long idPedido;
    private Long idCliente;
    private ClienteVentasResponse cliente;
    private String estadoPedido;
}
