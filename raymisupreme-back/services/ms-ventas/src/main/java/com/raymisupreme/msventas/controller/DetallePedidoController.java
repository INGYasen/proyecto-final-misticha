package com.raymisupreme.msventas.controller;

import com.raymisupreme.msventas.dto.DetallePedidoRequest;
import com.raymisupreme.msventas.dto.DetallePedidoResponse;
import com.raymisupreme.msventas.service.DetallePedidoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import com.raymisupreme.msventas.service.PedidoService;

@RestController
@RequestMapping("/api/v1/detalles")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;
    private final PedidoService pedidoService;

    public DetallePedidoController(DetallePedidoService detallePedidoService, PedidoService pedidoService) {
        this.detallePedidoService = detallePedidoService;
        this.pedidoService = pedidoService;
    }

    // Roles: usuario autenticado sin validar CLIENTE por ahora.
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DetallePedidoResponse> crear(@RequestBody DetallePedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detallePedidoService.crear(request));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan detalle de pedidos.
    @GetMapping("/{idDetalle}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<DetallePedidoResponse> obtenerPorId(@PathVariable Long idDetalle) {
        return ResponseEntity.ok(detallePedidoService.obtenerPorId(idDetalle));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DetallePedidoResponse>> listar(
            @RequestParam(value = "idPedido", required = false) Long idPedido,
            @AuthenticationPrincipal Jwt jwt) {
        if (idPedido == null) {
            if (!esPersonal()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes listar todos los detalles");
            }
            return ResponseEntity.ok(detallePedidoService.listar());
        }
        if (!esPersonal()) {
            pedidoService.verificarPropietario(idPedido, jwt.getSubject(), jwt.getClaimAsString("email"));
        }
        return ResponseEntity.ok(detallePedidoService.listarPorPedido(idPedido));
    }

    private boolean esPersonal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(rol ->
                "ROLE_SUPER_ADMIN".equals(rol.getAuthority()) || "ROLE_VENDEDORA".equals(rol.getAuthority()));
    }

    // Roles: solo SUPER_ADMIN actualiza detalles de pedido.
    @PutMapping("/{idDetalle}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<DetallePedidoResponse> actualizar(
            @PathVariable Long idDetalle,
            @RequestBody DetallePedidoRequest request
    ) {
        return ResponseEntity.ok(detallePedidoService.actualizar(idDetalle, request));
    }

    // Roles: solo SUPER_ADMIN elimina detalles de pedido.
    @DeleteMapping("/{idDetalle}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long idDetalle) {
        detallePedidoService.eliminar(idDetalle);
        return ResponseEntity.noContent().build();
    }
}
