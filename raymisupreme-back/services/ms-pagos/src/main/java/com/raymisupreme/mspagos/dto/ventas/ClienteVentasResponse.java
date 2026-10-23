package com.raymisupreme.mspagos.dto.ventas;

import lombok.Data;

@Data
public class ClienteVentasResponse {
    private Long idCliente;
    private String keycloakUserId;
}
