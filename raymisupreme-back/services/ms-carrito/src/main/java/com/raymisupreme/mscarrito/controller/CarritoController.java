package com.raymisupreme.mscarrito.controller;

import com.raymisupreme.mscarrito.dto.CarritoResponse;
import com.raymisupreme.mscarrito.dto.ItemCarritoRequest;
import com.raymisupreme.mscarrito.service.CarritoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/carrito", "/api/v1/carrito"})
@Tag(name = "Carrito", description = "Cesta de compras activa del cliente")
@Validated
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{clienteId}")
    public ResponseEntity<CarritoResponse> obtener(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(clienteId));
    }

    @PostMapping("/{clienteId}/items")
    public ResponseEntity<CarritoResponse> agregarItem(@PathVariable Long clienteId,
                                                         @Valid @RequestBody ItemCarritoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.agregarItem(clienteId, request));
    }

    @PutMapping("/{clienteId}/items/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(@PathVariable Long clienteId,
                                                                @PathVariable Long itemId,
                                                                @RequestParam @Min(0) Integer cantidad) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(clienteId, itemId, cantidad));
    }

    @DeleteMapping("/{clienteId}/items/{itemId}")
    public ResponseEntity<CarritoResponse> quitarItem(@PathVariable Long clienteId,
                                                        @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(clienteId, itemId, 0));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> vaciar(@PathVariable Long clienteId) {
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{clienteId}/convertir")
    public ResponseEntity<CarritoResponse> convertir(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.convertirCarrito(clienteId));
    }
}
