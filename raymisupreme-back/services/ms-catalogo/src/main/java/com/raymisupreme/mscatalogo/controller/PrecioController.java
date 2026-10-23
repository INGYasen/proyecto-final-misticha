package com.raymisupreme.mscatalogo.controller;

import com.raymisupreme.mscatalogo.dto.PrecioRequest;
import com.raymisupreme.mscatalogo.dto.PrecioResponse;
import com.raymisupreme.mscatalogo.service.PrecioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/precios/prendas", "/api/v1/precios/prendas"})
public class PrecioController {

    private final PrecioService precioService;

    public PrecioController(PrecioService precioService) {
        this.precioService = precioService;
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan precios operativos.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<PrecioResponse>> listarPrecios() {
        return ResponseEntity.ok(precioService.listarActivos());
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan precios operativos.
    @GetMapping("/{prendaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<PrecioResponse> obtenerPorPrendaId(@PathVariable Long prendaId) {
        return ResponseEntity.ok(precioService.obtenerPorPrendaId(prendaId));
    }

    // Roles: solo SUPER_ADMIN administra precios.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PrecioResponse> crearPrecio(@Valid @RequestBody PrecioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(precioService.crear(request));
    }

    // Roles: solo SUPER_ADMIN administra precios.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PrecioResponse> actualizarPrecio(@PathVariable Long id,
                                                           @Valid @RequestBody PrecioRequest request) {
        return ResponseEntity.ok(precioService.actualizar(id, request));
    }

    // Roles: solo SUPER_ADMIN elimina precios.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminarPrecio(@PathVariable Long id) {
        precioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
