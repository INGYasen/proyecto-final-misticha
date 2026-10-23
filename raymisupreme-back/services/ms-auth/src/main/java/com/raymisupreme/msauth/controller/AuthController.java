package com.raymisupreme.msauth.controller;

import com.raymisupreme.msauth.dto.AuthRequest;
import com.raymisupreme.msauth.dto.AuthResponse;
import com.raymisupreme.msauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de administración de usuarios.
 * Todos los endpoints requieren rol ROLE_SUPER_ADMIN.
 *
 * Accesible a través del Gateway en: /api/v1/auth/**
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Auth", description = "CRUD de auth en Keycloak (solo SUPER_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;

    // ── GET /api/v1/usuarios ─────────────────────────────────────────────────
    @GetMapping
    //@Operation(summary = "Listar todos los usuarios del realm")
    public ResponseEntity<List<AuthResponse>> listar() {
        return ResponseEntity.ok(authService.listarTodos());
    }

    // ── GET /api/v1/usuarios/{id} ────────────────────────────────────────────
    @GetMapping("/{id}")
    //@Operation(summary = "Obtener un usuario por su ID (UUID de Keycloak)")
    public ResponseEntity<AuthResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(authService.buscarPorId(id));
    }

    // ── POST /api/v1/usuarios ────────────────────────────────────────────────
    @PostMapping
    //@Operation(summary = "Crear un usuario nuevo en Keycloak")
    public ResponseEntity<AuthResponse> crear(@Valid @RequestBody AuthRequest request) {
        AuthResponse creado = authService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // ── PUT /api/v1/usuarios/{id} ────────────────────────────────────────────
    @PutMapping("/{id}")
    //@Operation(summary = "Actualizar datos y/o roles de un usuario")
    public ResponseEntity<AuthResponse> actualizar(
            @PathVariable String id,
            @Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.actualizar(id, request));
    }

    // ── DELETE /api/v1/usuarios/{id} ─────────────────────────────────────────
    @DeleteMapping("/{id}")
    //@Operation(summary = "Eliminar un usuario de Keycloak")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        authService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ── PATCH /api/v1/usuarios/{id}/estado ──────────────────────────────────
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar la cuenta de un usuario")
    public ResponseEntity<Map<String, String>> cambiarEstado(
            @PathVariable String id,
            @RequestParam boolean enabled) {
        authService.cambiarEstado(id, enabled);
        String msg = enabled ? "Usuario activado correctamente" : "Usuario desactivado correctamente";
        return ResponseEntity.ok(Map.of("mensaje", msg));
    }
}
