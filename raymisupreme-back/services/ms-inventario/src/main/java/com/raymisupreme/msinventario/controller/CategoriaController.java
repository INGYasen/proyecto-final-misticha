package com.raymisupreme.msinventario.controller;

import com.raymisupreme.msinventario.dto.CategoriaRequest;
import com.raymisupreme.msinventario.dto.CategoriaResponse;
import com.raymisupreme.msinventario.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Roles: solo SUPER_ADMIN crea categorias.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CategoriaResponse> create(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse created = categoriaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan categorias.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<CategoriaResponse>> getAll() {
        return ResponseEntity.ok(categoriaService.getAll());
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan categorias.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<CategoriaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CategoriaResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
