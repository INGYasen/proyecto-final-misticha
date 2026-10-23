package com.raymisupreme.mspagos.controller;

import com.raymisupreme.mspagos.dto.ComprobanteResponse;
import com.raymisupreme.mspagos.service.ComprobanteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos/comprobantes")
@Tag(name = "Comprobantes", description = "Consulta de comprobantes de pago")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;

    // Roles: solo SUPER_ADMIN consulta comprobantes por informacion financiera sensible.
    @GetMapping("/{pagoId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ComprobanteResponse> obtenerPorPagoId(@PathVariable Long pagoId) {
        return ResponseEntity.ok(comprobanteService.obtenerPorPagoId(pagoId));
    }
}
