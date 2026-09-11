package pe.edu.upeu.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.inventario.dto.StockRequest;
import pe.edu.upeu.inventario.dto.StockResponse;
import pe.edu.upeu.inventario.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Existencias por producto")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Lista todos los registros")
    public List<StockResponse> listar() {
        return stockService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un registro por id (404 si no existe)")
    public StockResponse obtener(@PathVariable Long id) {
        return stockService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro (400 si los datos son invalidos)")
    public StockResponse crear(@Valid @RequestBody StockRequest request) {
        return stockService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro existente")
    public StockResponse actualizar(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
        return stockService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un registro")
    public void eliminar(@PathVariable Long id) {
        stockService.eliminar(id);
    }
}
