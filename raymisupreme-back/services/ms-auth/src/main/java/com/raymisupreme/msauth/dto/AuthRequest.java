package com.raymisupreme.msauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear o actualizar un usuario administrativo en Keycloak.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    /**
     * Password en texto plano. Keycloak la hashea internamente.
     * Requerida en creacion, opcional en actualizacion.
     */
    @Size(min = 4, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    /**
     * Roles del realm a asignar, ej: ["SUPER_ADMIN", "VENDEDORA"].
     * En creacion, si esta vacio no se asigna ningun rol.
     */
    private List<String> roles;

    /** Si el usuario esta habilitado para iniciar sesion. */
    private boolean enabled = true;
}
