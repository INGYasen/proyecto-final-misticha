package com.raymisupreme.mscatalogo.controller;

import com.raymisupreme.mscatalogo.dto.ProductoCatalogoResponse;
import com.raymisupreme.mscatalogo.service.CatalogoPublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoProductoController {

    private final CatalogoPublicService catalogoPublicService;

    public CatalogoProductoController(CatalogoPublicService catalogoPublicService) {
        this.catalogoPublicService = catalogoPublicService;
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoCatalogoResponse>> listarProductos(
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) String publico,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String talla) {
        return ResponseEntity.ok(catalogoPublicService.listarProductosCatalogo(idCategoria, publico, material, color, talla));
    }

    @GetMapping("/productos/{idPrenda}")
    public ResponseEntity<ProductoCatalogoResponse> obtenerProducto(@PathVariable Long idPrenda) {
        return ResponseEntity.ok(catalogoPublicService.obtenerProductoCatalogo(idPrenda));
    }
}
