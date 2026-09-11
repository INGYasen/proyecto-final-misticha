package pe.edu.upeu.pago.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.pago.dto.TransaccionRequest;
import pe.edu.upeu.pago.dto.TransaccionResponse;
import pe.edu.upeu.pago.service.TransaccionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Movimientos del medio de pago")
public class TransaccionController {

    private final TransaccionService transaccionService;

    @GetMapping
    @Operation(summary = "Lista todos los registros")
    public List<TransaccionResponse> listar() {
        return transaccionService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un registro por id (404 si no existe)")
    public TransaccionResponse obtener(@PathVariable Long id) {
        return transaccionService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro (400 si los datos son invalidos)")
    public TransaccionResponse crear(@Valid @RequestBody TransaccionRequest request) {
        return transaccionService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro existente")
    public TransaccionResponse actualizar(@PathVariable Long id, @Valid @RequestBody TransaccionRequest request) {
        return transaccionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un registro")
    public void eliminar(@PathVariable Long id) {
        transaccionService.eliminar(id);
    }
}
