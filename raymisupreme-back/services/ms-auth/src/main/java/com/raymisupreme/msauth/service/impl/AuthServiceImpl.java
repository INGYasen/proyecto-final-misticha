package com.raymisupreme.msauth.service.impl;

import com.raymisupreme.msauth.dto.AuthRequest;
import com.raymisupreme.msauth.dto.AuthResponse;
import com.raymisupreme.msauth.entity.AuthEntity;
import com.raymisupreme.msauth.exception.AuthNotFoundException;
import com.raymisupreme.msauth.mapper.AuthMapper;
import com.raymisupreme.msauth.service.AuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Set<String> BUSINESS_ROLES = Set.of("SUPER_ADMIN", "VENDEDORA", "CLIENTE");

    private final Keycloak keycloak;
    private final String keycloakRealm;
    private final AuthMapper authMapper;

    // ── Helpers ──────────────────────────────────────────────────────────────

    private RealmResource getRealm() {
        return keycloak.realm(keycloakRealm);
    }

    private boolean esSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(rol -> "ROLE_SUPER_ADMIN".equals(rol.getAuthority()));
    }

    private UsersResource getUsersResource() {
        return getRealm().users();
    }

    /**
     * Carga los roles de negocio de un usuario desde Keycloak
     * y construye el UsuarioEntity usando el mapper.
     */
    private AuthEntity toEntity(UserRepresentation user) {
        List<String> roles = Collections.emptyList();
        try {
            roles = getRealm().users().get(user.getId())
                    .roles().realmLevel().listAll()
                    .stream()
                    .map(RoleRepresentation::getName)
                    .filter(BUSINESS_ROLES::contains)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("No se pudieron cargar roles del usuario {}: {}", user.getId(), e.getMessage());
        }
        return authMapper.toEntity(user, roles);
    }

    // ── Listar ───────────────────────────────────────────────────────────────

    @Override
    public List<AuthResponse> listarTodos() {
        return getUsersResource().list()
                .stream()
                .map(this::toEntity)
                .map(authMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── Buscar por ID ────────────────────────────────────────────────────────

    @Override
    public AuthResponse buscarPorId(String id) {
        try {
            UserRepresentation user = getUsersResource().get(id).toRepresentation();
            return authMapper.toResponse(toEntity(user));
        } catch (Exception e) {
            throw new AuthNotFoundException("Usuario no encontrado con id: " + id);
        }
    }

    // ── Crear ────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse crear(AuthRequest request) {
        UserRepresentation user = authMapper.toKeycloakUser(request);

        Response response = getUsersResource().create(user);

        if (response.getStatus() != 201) {
            String body = response.readEntity(String.class);
            throw new RuntimeException("Error al crear usuario en Keycloak. Status: "
                    + response.getStatus() + " — " + body);
        }

        // Extraer el ID desde el header Location
        String location = response.getHeaderString("Location");
        String userId = location.substring(location.lastIndexOf('/') + 1);

        // Asignar contraseña
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            getUsersResource().get(userId).resetPassword(
                    authMapper.toCredential(request.getPassword())
            );
        }

        // El registro público solo puede crear clientes. Un visitante no elige su rol.
        List<String> roles = esSuperAdmin()
                ? ((request.getRoles() != null && !request.getRoles().isEmpty()) ? request.getRoles() : List.of("CLIENTE"))
                : List.of("CLIENTE");

        asignarRoles(userId, roles);

        log.info("Usuario creado en Keycloak: {} (id={})", request.getUsername(), userId);
        return buscarPorId(userId);
    }

    // ── Actualizar ───────────────────────────────────────────────────────────

    @Override
    public AuthResponse actualizar(String id, AuthRequest request) {
        UserResource userResource;
        try {
            userResource = getUsersResource().get(id);
            userResource.toRepresentation(); // verifica que existe
        } catch (Exception e) {
            throw new AuthNotFoundException("Usuario no encontrado con id: " + id);
        }

        UserRepresentation user = userResource.toRepresentation();
        if (request.getEmail() != null)     user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)  user.setLastName(request.getLastName());
        user.setEnabled(request.isEnabled());

        userResource.update(user);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userResource.resetPassword(authMapper.toCredential(request.getPassword()));
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll()
                    .stream()
                    .filter(r -> BUSINESS_ROLES.contains(r.getName()))
                    .collect(Collectors.toList());
            if (!currentRoles.isEmpty()) {
                userResource.roles().realmLevel().remove(currentRoles);
            }
            asignarRoles(id, request.getRoles());
        }

        log.info("Usuario actualizado en Keycloak: id={}", id);
        return buscarPorId(id);
    }

    // ── Eliminar ─────────────────────────────────────────────────────────────

    @Override
    public void eliminar(String id) {
        try {
            getUsersResource().get(id).toRepresentation();
        } catch (Exception e) {
            throw new AuthNotFoundException("Usuario no encontrado con id: " + id);
        }
        getUsersResource().delete(id);
        log.info("Usuario eliminado de Keycloak: id={}", id);
    }

    // ── Cambiar estado ───────────────────────────────────────────────────────

    @Override
    public void cambiarEstado(String id, boolean enabled) {
        UserResource userResource;
        try {
            userResource = getUsersResource().get(id);
            userResource.toRepresentation();
        } catch (Exception e) {
            throw new AuthNotFoundException("Usuario no encontrado con id: " + id);
        }

        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(enabled);
        userResource.update(user);
        log.info("Estado del usuario {} cambiado a: {}", id, enabled ? "ACTIVO" : "DESACTIVADO");
    }

    // ── Asignar roles ────────────────────────────────────────────────────────

    private void asignarRoles(String userId, List<String> roleNames) {
        RealmResource realm = getRealm();

        List<RoleRepresentation> rolesToAssign = roleNames.stream()
                .map(roleName -> roleName.startsWith("ROLE_") ? roleName.substring("ROLE_".length()) : roleName)
                .filter(BUSINESS_ROLES::contains)
                .map(roleName -> {
                    try {
                        return realm.roles().get(roleName).toRepresentation();
                    } catch (Exception e) {
                        // Evita usuarios creados sin rol cuando el realm de
                        // desarrollo fue importado con una configuración antigua.
                        RoleRepresentation nuevoRol = new RoleRepresentation();
                        nuevoRol.setName(roleName);
                        nuevoRol.setDescription("Rol de negocio " + roleName);
                        realm.roles().create(nuevoRol);
                        log.info("Rol '{}' creado automáticamente en Keycloak", roleName);
                        return realm.roles().get(roleName).toRepresentation();
                    }
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        if (!rolesToAssign.isEmpty()) {
            realm.users().get(userId).roles().realmLevel().add(rolesToAssign);
        }
    }
}
