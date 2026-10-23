package com.raymisupreme.msauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta con los datos de un usuario de Keycloak.
 * No expone la contraseña.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** ID único en Keycloak (UUID) */
    private String id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;

    /** Roles del realm asignados al usuario */
    private List<String> roles;
}
