package com.raymisupreme.mscatalogo.controller;

import com.raymisupreme.mscatalogo.dto.DescuentoRequest;
import com.raymisupreme.mscatalogo.dto.DescuentoResponse;
import com.raymisupreme.mscatalogo.service.DescuentoService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping({"/api/precios/descuentos", "/api/v1/precios/descuentos"})
public class DescuentoController {

    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    // Roles: SUPER_ADMIN y VENDEDORA consultan descuentos operativos.
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','VENDEDORA')")
    public ResponseEntity<List<DescuentoResponse>> listarDescuentos() {
        return ResponseEntity.ok(descuentoService.listarTodos());
    }

    // Roles: solo SUPER_ADMIN administra descuentos.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<DescuentoResponse> crearDescuento(@Valid @RequestBody DescuentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(descuentoService.crear(request));
    }

    // Roles: solo SUPER_ADMIN administra descuentos.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<DescuentoResponse> actualizarDescuento(@PathVariable Long id,
                                                                 @Valid @RequestBody DescuentoRequest request) {
        return ResponseEntity.ok(descuentoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminarDescuento(@PathVariable Long id) {
        descuentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
