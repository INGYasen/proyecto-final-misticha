package com.raymisupreme.msauth.service;

import com.raymisupreme.msauth.dto.AuthRequest;
import com.raymisupreme.msauth.dto.AuthResponse;

import java.util.List;

public interface AuthService {

    /** Lista todos los usuarios del realm */
    List<AuthResponse> listarTodos();

    /** Busca un usuario por su ID (UUID de Keycloak) */
    AuthResponse buscarPorId(String id);

    /** Crea un usuario en Keycloak con la contraseña y roles indicados */
    AuthResponse crear(AuthRequest request);

    /** Actualiza datos básicos y roles de un usuario */
    AuthResponse actualizar(String id, AuthRequest request);

    /** Elimina el usuario de Keycloak */
    void eliminar(String id);

    /** Activa o desactiva la cuenta del usuario */
    void cambiarEstado(String id, boolean enabled);
}
