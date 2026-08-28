package pe.edu.upeu.orden.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.service.OrdenService;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@Tag(name = "Ordenes", description = "Gestion de ordenes de compra del dominio orden-ms")
public class OrdenController {

    private final OrdenService ordenService;

    @GetMapping
    @Operation(summary = "Lista todas las ordenes")
    public List<OrdenResponse> listar() {
        return ordenService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una orden por id (404 si no existe)")
    public OrdenResponse obtener(@PathVariable Long id) {
        return ordenService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una orden (400 si los datos son invalidos, 409 si el codigo ya existe)")
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest request) {
        return ordenService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una orden existente")
    public OrdenResponse actualizar(@PathVariable Long id, @Valid @RequestBody OrdenRequest request) {
        return ordenService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina una orden y sus detalles")
    public void eliminar(@PathVariable Long id) {
        ordenService.eliminar(id);
    }
}
