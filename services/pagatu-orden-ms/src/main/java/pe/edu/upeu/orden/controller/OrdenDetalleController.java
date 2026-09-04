package pe.edu.upeu.orden.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.orden.dto.OrdenDetalleRequest;
import pe.edu.upeu.orden.dto.OrdenDetalleResponse;
import pe.edu.upeu.orden.service.OrdenDetalleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orden-detalles")
@RequiredArgsConstructor
@Tag(name = "Detalles de orden", description = "Lineas de cada orden; referencian por id a los productos de pagatu-catalogo-ms")
public class OrdenDetalleController {

    private final OrdenDetalleService ordenDetalleService;

    @GetMapping
    @Operation(summary = "Lista los detalles; admite filtro opcional por ordenId")
    public List<OrdenDetalleResponse> listar(@RequestParam(required = false) Long ordenId) {
        return ordenDetalleService.listar(ordenId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un detalle por id (404 si no existe)")
    public OrdenDetalleResponse obtener(@PathVariable Long id) {
        return ordenDetalleService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agrega un detalle a una orden y recalcula su total")
    public OrdenDetalleResponse crear(@Valid @RequestBody OrdenDetalleRequest request) {
        return ordenDetalleService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un detalle y recalcula el total de la orden")
    public OrdenDetalleResponse actualizar(@PathVariable Long id, @Valid @RequestBody OrdenDetalleRequest request) {
        return ordenDetalleService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un detalle y recalcula el total de la orden")
    public void eliminar(@PathVariable Long id) {
        ordenDetalleService.eliminar(id);
    }
}
