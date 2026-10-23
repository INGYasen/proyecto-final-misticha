package com.raymisupreme.msventas.controller;

import com.raymisupreme.msventas.dto.CheckoutResponse;
import com.raymisupreme.msventas.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentasCheckoutController {

    private final PedidoService pedidoService;

    public VentasCheckoutController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/checkout/{idCliente}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pedidoService.checkout(idCliente));
    }
}
