package com.raymisupreme.mscatalogo.controller;

import com.raymisupreme.mscatalogo.dto.ItemCatalogoRequest;
import com.raymisupreme.mscatalogo.dto.ItemCatalogoResponse;
import com.raymisupreme.mscatalogo.service.ItemCatalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo/items")
public class ItemCatalogoController {

    @Autowired
    private ItemCatalogoService service;

    // Roles: solo SUPER_ADMIN administra items del catalogo.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ItemCatalogoResponse> crear(@Valid @RequestBody ItemCatalogoRequest request) {
        return ResponseEntity.ok(service.guardar(request));
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan items del catalogo.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<ItemCatalogoResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan items del catalogo.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<ItemCatalogoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ItemCatalogoResponse> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ItemCatalogoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // Roles: solo SUPER_ADMIN elimina items del catalogo.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
