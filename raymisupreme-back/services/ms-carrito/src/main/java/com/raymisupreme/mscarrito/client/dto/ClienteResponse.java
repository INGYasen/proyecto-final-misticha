package com.raymisupreme.mscarrito.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long idCliente;
    private String nombres;
    private String email;
    private String pais;
    private String ciudad;
    private String direccionEnvio;
}
