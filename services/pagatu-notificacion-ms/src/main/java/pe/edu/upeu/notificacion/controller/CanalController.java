package pe.edu.upeu.notificacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.notificacion.dto.CanalRequest;
import pe.edu.upeu.notificacion.dto.CanalResponse;
import pe.edu.upeu.notificacion.service.CanalService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/canales")
@RequiredArgsConstructor
@Tag(name = "Canales", description = "Email, WhatsApp u otros canales")
public class CanalController {

    private final CanalService canalService;

    @GetMapping
    @Operation(summary = "Lista todos los registros")
    public List<CanalResponse> listar() {
        return canalService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un registro por id (404 si no existe)")
    public CanalResponse obtener(@PathVariable Long id) {
        return canalService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un registro (400 si los datos son invalidos)")
    public CanalResponse crear(@Valid @RequestBody CanalRequest request) {
        return canalService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un registro existente")
    public CanalResponse actualizar(@PathVariable Long id, @Valid @RequestBody CanalRequest request) {
        return canalService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un registro")
    public void eliminar(@PathVariable Long id) {
        canalService.eliminar(id);
    }
}
