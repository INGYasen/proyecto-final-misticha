package com.raymisupreme.msauth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Modelo interno de usuario tal como se administra en Keycloak.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthEntity {

    /** ID unico asignado por Keycloak (UUID). */
    private String id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    /** true = el usuario puede iniciar sesion. */
    private boolean enabled;

    /** Roles del realm asignados al usuario (ej: SUPER_ADMIN, VENDEDORA). */
    private List<String> roles;
}
