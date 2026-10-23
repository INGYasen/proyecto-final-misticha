package com.raymisupreme.msacopio.controller;

import com.raymisupreme.msacopio.dto.ArtesanoResponse;
import com.raymisupreme.msacopio.dto.ArtesanoRequest;
import com.raymisupreme.msacopio.service.ArtesanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artesanos")
public class ArtesanoController {

    @Autowired
    private ArtesanoService service;

    // Roles: solo SUPER_ADMIN registra artesanos como datos de acopio; no tienen login.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ArtesanoResponse> crear(@RequestBody ArtesanoRequest request) {
        return ResponseEntity.ok(service.guardar(request));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan artesanos registrados.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<ArtesanoResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan artesanos registrados.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<ArtesanoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ArtesanoResponse> actualizar(@PathVariable Long id,
                                                       @RequestBody ArtesanoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // Roles: solo SUPER_ADMIN elimina registros de artesanos.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
