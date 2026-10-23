package com.raymisupreme.mspagos.controller;

import com.raymisupreme.mspagos.dto.StripeCheckoutRequest;
import com.raymisupreme.mspagos.dto.StripeCheckoutResponse;
import com.raymisupreme.mspagos.dto.StripeSessionResponse;
import com.raymisupreme.mspagos.service.StripeCheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos/stripe")
public class StripeCheckoutController {
    private final StripeCheckoutService service;
    public StripeCheckoutController(StripeCheckoutService service) { this.service = service; }

    @PostMapping("/checkout-session")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StripeCheckoutResponse> crear(@Valid @RequestBody StripeCheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSesion(request.ventaId()));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StripeSessionResponse> consultar(@PathVariable String sessionId) {
        return ResponseEntity.ok(service.consultarSesion(sessionId));
    }
}
