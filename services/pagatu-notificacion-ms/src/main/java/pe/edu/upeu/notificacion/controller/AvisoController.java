package pe.edu.upeu.notificacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.notificacion.dto.AvisoRequest;
import pe.edu.upeu.notificacion.dto.AvisoResponse;
import pe.edu.upeu.notificacion.service.AvisoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avisos")
@RequiredArgsConstructor
@Tag(name = "Avisos", description = "Mensajes enviados al cliente")
public class AvisoController {

    private final AvisoService avisoService;

    @GetMapping
    @Operation(summary = "Lista todos los registros")
    public List<AvisoResponse> listar() {
        return avisoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un registro por id (404 si no existe)")
    public AvisoResponse obtener(@PathVariable Long id) {
        return avisoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro (400 si los datos son invalidos)")
    public AvisoResponse crear(@Valid @RequestBody AvisoRequest request) {
        return avisoService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro existente")
    public AvisoResponse actualizar(@PathVariable Long id, @Valid @RequestBody AvisoRequest request) {
        return avisoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un registro")
    public void eliminar(@PathVariable Long id) {
        avisoService.eliminar(id);
    }
}
