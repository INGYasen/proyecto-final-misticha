package com.raymisupreme.msventas.controller;

import com.raymisupreme.msventas.dto.ClienteRequest;
import com.raymisupreme.msventas.dto.ClienteResponse;
import com.raymisupreme.msventas.service.ClienteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Roles: usuario autenticado sin validar CLIENTE por ahora.
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest request,
                                                  @AuthenticationPrincipal Jwt jwt) {
        request.setKeycloakUserId(jwt.getSubject());
        request.setUsername(jwt.getClaimAsString("preferred_username"));
        request.setEmail(jwt.getClaimAsString("email"));
        if (request.getNombres() == null || request.getNombres().isBlank()) {
            request.setNombres(jwt.getClaimAsString("given_name"));
        }
        if (request.getApellidos() == null || request.getApellidos().isBlank()) {
            request.setApellidos(jwt.getClaimAsString("family_name"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(request));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteResponse> obtenerActual(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(clienteService.obtenerActual(jwt.getSubject(), jwt.getClaimAsString("email")));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan clientes para atencion de ventas.
    @GetMapping("/{idCliente}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.obtenerPorId(idCliente));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan clientes para atencion de ventas.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    // Roles: solo SUPER_ADMIN actualiza clientes desde administracion.
    @PutMapping("/{idCliente}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long idCliente, @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(idCliente, request));
    }

    // Roles: solo SUPER_ADMIN elimina clientes.
    @DeleteMapping("/{idCliente}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long idCliente) {
        clienteService.eliminar(idCliente);
        return ResponseEntity.noContent().build();
    }
}
