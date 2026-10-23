package com.raymisupreme.msventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {
    private String nombres;
    private String apellidos;
    private String username;
    private String email;
    private String pais;
    private String ciudad;
    private String direccionEnvio;
    private String keycloakUserId;
}
