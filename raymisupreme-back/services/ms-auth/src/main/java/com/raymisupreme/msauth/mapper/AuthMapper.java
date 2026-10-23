package com.raymisupreme.msauth.mapper;

import com.raymisupreme.msauth.dto.AuthRequest;
import com.raymisupreme.msauth.dto.AuthResponse;
import com.raymisupreme.msauth.entity.AuthEntity;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Convierte entre las tres capas de representación de un usuario:
 *
 *  AuthRequest  ──►  UserRepresentation  (para enviar a Keycloak)
 *  UserRepresentation ──►  AuthEntity    (modelo de dominio interno)
 *  AuthEntity      ──►  AuthResponse  (DTO de respuesta API)
 *
 * Sigue el mismo patrón que ArtesanoMapper / PrendaMapper en los demás
 * microservicios del proyecto.
 */
@Component
public class AuthMapper {

    // ── Request → UserRepresentation (para crear/actualizar en Keycloak) ─────

    /**
     * Convierte el DTO de entrada en la representación que espera la Admin API
     * de Keycloak. NO asigna contraseña ni roles aquí — eso lo gestiona el
     * servicio mediante llamadas separadas a la API de Keycloak.
     */
    public UserRepresentation toKeycloakUser(AuthRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(request.isEnabled());
        user.setEmailVerified(true);
        return user;
    }

    /**
     * Construye la credencial de contraseña para Keycloak (no temporal).
     */
    public CredentialRepresentation toCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        return credential;
    }

    // ── UserRepresentation → AuthEntity ───────────────────────────────────

    /**
     * Convierte la representación cruda de Keycloak al modelo de dominio
     * interno. Los roles se pasan por separado porque Keycloak los devuelve
     * en una llamada distinta a la Admin API.
     */
    public AuthEntity toEntity(UserRepresentation user, List<String> roles) {
        return AuthEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(Boolean.TRUE.equals(user.isEnabled()))
                .roles(roles != null ? roles : Collections.emptyList())
                .build();
    }

    // ── AuthEntity → AuthResponse ─────────────────────────────────────

    /**
     * Convierte el modelo de dominio al DTO que se devuelve por la API REST.
     */
    public AuthResponse toResponse(AuthEntity entity) {
        return AuthResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .enabled(entity.isEnabled())
                .roles(entity.getRoles())
                .build();
    }

    // ── Atajo: UserRepresentation + roles → AuthResponse ─────────────────

    /**
     * Conversión directa sin pasar por AuthEntity explícitamente.
     * Útil cuando no se necesita manipular el entity.
     */
    public AuthResponse toResponse(UserRepresentation user, List<String> roles) {
        return toResponse(toEntity(user, roles));
    }
}