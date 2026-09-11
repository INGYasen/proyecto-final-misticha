package pe.edu.upeu.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.inventario.dto.MovimientoRequest;
import pe.edu.upeu.inventario.dto.MovimientoResponse;
import pe.edu.upeu.inventario.service.MovimientoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Ingresos, salidas y ajustes de stock")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    @Operation(summary = "Lista todos los registros")
    public List<MovimientoResponse> listar() {
        return movimientoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un registro por id (404 si no existe)")
    public MovimientoResponse obtener(@PathVariable Long id) {
        return movimientoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro (400 si los datos son invalidos)")
    public MovimientoResponse crear(@Valid @RequestBody MovimientoRequest request) {
        return movimientoService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro existente")
    public MovimientoResponse actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoRequest request) {
        return movimientoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un registro")
    public void eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
    }
}
