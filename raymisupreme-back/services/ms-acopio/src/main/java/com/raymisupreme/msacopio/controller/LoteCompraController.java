package com.raymisupreme.msacopio.controller;

import com.raymisupreme.msacopio.dto.LoteCompraRequest;
import com.raymisupreme.msacopio.dto.LoteCompraResponse;
import com.raymisupreme.msacopio.service.LoteCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lotes")
public class LoteCompraController {

    @Autowired
    private LoteCompraService service;

    // Roles: solo SUPER_ADMIN registra lotes de acopio.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<LoteCompraResponse> guardar(@RequestBody LoteCompraRequest request) {
        return ResponseEntity.ok(service.registrarLote(request));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan lotes de acopio.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<LoteCompraResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan lotes de acopio.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<LoteCompraResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<LoteCompraResponse> actualizar(@PathVariable Long id,
                                                         @RequestBody LoteCompraRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
