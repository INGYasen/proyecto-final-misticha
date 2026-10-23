package com.raymisupreme.msinventario.controller;

import com.raymisupreme.msinventario.dto.PrendaRequest;
import com.raymisupreme.msinventario.dto.PrendaDetalleCompletoResponse;
import com.raymisupreme.msinventario.dto.PrendaResponse;
import com.raymisupreme.msinventario.dto.StockResponse;
import com.raymisupreme.msinventario.service.PrendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/prendas")
public class PrendaController {

    private final PrendaService prendaService;
    private final String internalPaymentSecret;

    public PrendaController(PrendaService prendaService,
                            @Value("${internal.payment-secret}") String internalPaymentSecret) {
        this.prendaService = prendaService;
        this.internalPaymentSecret = internalPaymentSecret;
    }

    // Roles: solo SUPER_ADMIN registra prendas en inventario.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PrendaResponse> create(@Valid @RequestBody PrendaRequest request) {
        PrendaResponse created = prendaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan stock/prendas.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<PrendaResponse>> getAll(
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) PublicoObjetivo publico) {
        return ResponseEntity.ok(prendaService.getAll(idCategoria, publico));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<PrendaResponse> getPublicById(@PathVariable Long id) {
        return ResponseEntity.ok(prendaService.getById(id));
    }

    /** Consulta publica en lote para construir el catalogo sin una llamada por producto. */
    @GetMapping("/public")
    public ResponseEntity<List<PrendaResponse>> getAllPublic() {
        return ResponseEntity.ok(prendaService.getAll(null, null));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan stock/prendas.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<PrendaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prendaService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PrendaResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody PrendaRequest request) {
        return ResponseEntity.ok(prendaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prendaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan prenda con detalle del lote de acopio.
    @GetMapping("/{id}/detalle-completo")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<PrendaDetalleCompletoResponse> getDetalleCompleto(@PathVariable Long id) {
        return ResponseEntity.ok(prendaService.getDetalleCompleto(id));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan stock puntual.
    @GetMapping("/{id}/stock")
    public ResponseEntity<StockResponse> getStock(@PathVariable Long id,
            @RequestHeader(value = "X-Internal-Payment-Secret", required = false) String secret,
            Authentication authentication) {
        validarAccesoInterno(secret, authentication);
        return ResponseEntity.ok(prendaService.getStock(id));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan stock/prendas por lote.
    @GetMapping("/lote/{idLote}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<PrendaResponse>> getByIdLote(@PathVariable Long idLote) {
        return ResponseEntity.ok(prendaService.getByIdLote(idLote));
    }

    // Roles: solo SUPER_ADMIN modifica stock directamente.
    @PutMapping("/{id}/descontar")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id, @RequestParam Integer cantidad,
            @RequestHeader(value = "X-Internal-Payment-Secret", required = false) String secret,
            Authentication authentication) {
        validarAccesoInterno(secret, authentication);
        prendaService.descontarStock(id, cantidad);
        return ResponseEntity.noContent().build();
    }

    private void validarAccesoInterno(String secret, Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(rol -> "ROLE_SUPER_ADMIN".equals(rol.getAuthority()));
        boolean secretoValido = secret != null && MessageDigest.isEqual(
                internalPaymentSecret.getBytes(StandardCharsets.UTF_8),
                secret.getBytes(StandardCharsets.UTF_8));
        if (!admin && !secretoValido) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }


}
