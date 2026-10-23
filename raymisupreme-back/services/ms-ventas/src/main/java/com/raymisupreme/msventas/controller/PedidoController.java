package com.raymisupreme.msventas.controller;

import com.raymisupreme.msventas.dto.ConfirmarPedidoRequest;
import com.raymisupreme.msventas.dto.PedidoRequest;
import com.raymisupreme.msventas.dto.PedidoResponse;
import com.raymisupreme.msventas.service.PedidoService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final String internalPaymentSecret;

    public PedidoController(PedidoService pedidoService,
                            @Value("${internal.payment-secret}") String internalPaymentSecret) {
        this.pedidoService = pedidoService;
        this.internalPaymentSecret = internalPaymentSecret;
    }

    // Roles: usuario autenticado sin validar CLIENTE por ahora.
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> crear(@RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request));
    }

    @GetMapping("/mios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponse>> mios(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pedidoService.listarDelUsuario(
                jwt.getSubject(), jwt.getClaimAsString("email")));
    }

    // Roles: el dueño del pedido y el personal de tienda consultan un pedido.
    @GetMapping("/{idPedido:\\d+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long idPedido,
                                                       @AuthenticationPrincipal Jwt jwt) {
        if (!esPersonal()) {
            pedidoService.verificarPropietario(idPedido, jwt.getSubject(), jwt.getClaimAsString("email"));
        }
        return ResponseEntity.ok(pedidoService.obtenerPorId(idPedido));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan pedidos/ventas.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<PedidoResponse>> listar(@RequestParam(value = "idCliente", required = false) Long idCliente) {
        if (idCliente != null) {
            return ResponseEntity.ok(pedidoService.listarPorCliente(idCliente));
        }
        return ResponseEntity.ok(pedidoService.listar());
    }

    // Roles: solo SUPER_ADMIN actualiza el pedido completo.
    @PutMapping("/{idPedido}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PedidoResponse> actualizar(@PathVariable Long idPedido, @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizar(idPedido, request));
    }

    @PostMapping("/{idPedido}/confirmar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable Long idPedido,
                                                    @Valid @RequestBody ConfirmarPedidoRequest request,
                                                    @AuthenticationPrincipal Jwt jwt) {
        if (!esSuperAdmin()) {
            pedidoService.verificarPropietario(idPedido, jwt.getSubject(), jwt.getClaimAsString("email"));
        }
        return ResponseEntity.ok(pedidoService.confirmarPedido(idPedido, request));
    }

    @PostMapping("/{idPedido}/confirmar-pago-interno")
    public ResponseEntity<PedidoResponse> confirmarPagoInterno(
            @PathVariable Long idPedido,
            @RequestHeader("X-Internal-Payment-Secret") String secret) {
        boolean valido = MessageDigest.isEqual(
                internalPaymentSecret.getBytes(StandardCharsets.UTF_8),
                secret.getBytes(StandardCharsets.UTF_8));
        if (!valido) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(pedidoService.confirmarPagoMercadoPago(idPedido));
    }

    @PostMapping("/{idPedido}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long idPedido,
                                                   @AuthenticationPrincipal Jwt jwt) {
        if (esVendedora()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!esSuperAdmin()) {
            pedidoService.verificarPropietario(idPedido, jwt.getSubject(), jwt.getClaimAsString("email"));
        }
        return ResponseEntity.ok(pedidoService.cancelarPedido(idPedido));
    }

    // Roles: solo SUPER_ADMIN elimina/anula totalmente pedidos.
    private boolean esPersonal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(rol ->
                "ROLE_SUPER_ADMIN".equals(rol.getAuthority()) || "ROLE_VENDEDORA".equals(rol.getAuthority()));
    }

    private boolean esVendedora() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(rol -> "ROLE_VENDEDORA".equals(rol.getAuthority()));
    }

    private boolean esSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(rol -> "ROLE_SUPER_ADMIN".equals(rol.getAuthority()));
    }

    @DeleteMapping("/{idPedido}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long idPedido) {
        pedidoService.eliminar(idPedido);
        return ResponseEntity.noContent().build();
    }
}
