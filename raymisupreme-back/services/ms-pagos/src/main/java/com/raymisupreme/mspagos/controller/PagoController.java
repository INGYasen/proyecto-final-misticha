package com.raymisupreme.mspagos.controller;

import com.raymisupreme.mspagos.dto.PagoEstadoRequest;
import com.raymisupreme.mspagos.dto.PagoRequest;
import com.raymisupreme.mspagos.dto.PagoResponse;
import com.raymisupreme.mspagos.dto.MercadoPagoPreferenciaRequest;
import com.raymisupreme.mspagos.dto.MercadoPagoPreferenciaResponse;
import com.raymisupreme.mspagos.dto.MercadoPagoPaymentRequest;
import com.raymisupreme.mspagos.dto.MercadoPagoPaymentResponse;
import com.raymisupreme.mspagos.service.MercadoPagoService;
import com.raymisupreme.mspagos.service.PagoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping({"/api/pagos", "/api/v1/pagos"})
@Tag(name = "Pagos", description = "Procesamiento de pagos y consultas")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/mercadopago/preferencia")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MercadoPagoPreferenciaResponse> crearPreferencia(
            @Valid @RequestBody MercadoPagoPreferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mercadoPagoService.crearPreferencia(request.getPedidoId()));
    }

    @PostMapping("/mercadopago/payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MercadoPagoPaymentResponse> crearPago(
            @Valid @RequestBody MercadoPagoPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mercadoPagoService.crearPago(request));
    }

    @PostMapping("/mercadopago/webhook")
    public ResponseEntity<Void> webhookMercadoPago(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String id,
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestHeader(name = "x-request-id", required = false) String requestId,
            @RequestHeader(name = "x-signature", required = false) String signature,
            @RequestBody(required = false) Map<String, Object> payload) {
        if (!mercadoPagoService.firmaWebhookValida(dataId != null ? dataId : id, requestId, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        mercadoPagoService.procesarWebhook(type, topic, id,
                payload == null ? Collections.emptyMap() : payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/procesar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PagoResponse> procesar(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesarPago(request));
    }

    // Roles: solo SUPER_ADMIN consulta pagos por informacion financiera sensible.
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PagoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    // Roles: solo SUPER_ADMIN consulta pagos por informacion financiera sensible.
    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(rol -> "ROLE_SUPER_ADMIN".equals(rol.getAuthority()));
        if (admin) return ResponseEntity.ok(pagoService.obtenerPorPedidoId(pedidoId));
        return ResponseEntity.ok(mercadoPagoService.consultarPagoPropio(pedidoId));
    }

    // Roles: solo SUPER_ADMIN actualiza estados financieros.
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PagoResponse> actualizarEstado(@PathVariable Long id,
                                                           @Valid @RequestBody PagoEstadoRequest request) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, request));
    }

    // Roles: solo SUPER_ADMIN lista pagos/reportes financieros.
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PagoResponse>> listar() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }
}
