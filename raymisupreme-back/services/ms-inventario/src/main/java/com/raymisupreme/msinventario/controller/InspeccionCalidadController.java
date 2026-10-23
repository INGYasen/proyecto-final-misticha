package com.raymisupreme.msinventario.controller;

import com.raymisupreme.msinventario.dto.InspeccionCalidadRequest;
import com.raymisupreme.msinventario.dto.InspeccionCalidadResponse;
import com.raymisupreme.msinventario.service.InspeccionCalidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inspecciones")
public class InspeccionCalidadController {

    private final InspeccionCalidadService inspeccionCalidadService;

    public InspeccionCalidadController(InspeccionCalidadService inspeccionCalidadService) {
        this.inspeccionCalidadService = inspeccionCalidadService;
    }

    // Roles: solo SUPER_ADMIN registra inspecciones.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<InspeccionCalidadResponse> create(@Valid @RequestBody InspeccionCalidadRequest request) {
        InspeccionCalidadResponse created = inspeccionCalidadService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan inspecciones.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<InspeccionCalidadResponse>> getAll() {
        return ResponseEntity.ok(inspeccionCalidadService.getAll());
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan inspecciones.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<InspeccionCalidadResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inspeccionCalidadService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<InspeccionCalidadResponse> update(@PathVariable Long id,
                                                            @Valid @RequestBody InspeccionCalidadRequest request) {
        return ResponseEntity.ok(inspeccionCalidadService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inspeccionCalidadService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan inspecciones por prenda.
    @GetMapping("/prenda/{idPrenda}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<InspeccionCalidadResponse>> getByIdPrenda(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(inspeccionCalidadService.getByIdPrenda(idPrenda));
    }
}
