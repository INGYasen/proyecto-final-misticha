package com.raymisupreme.mscatalogo.controller;

import com.raymisupreme.mscatalogo.dto.CategoriaPublicDTO;
import com.raymisupreme.mscatalogo.dto.HomePublicDTO;
import com.raymisupreme.mscatalogo.dto.ProductoPublicDTO;
import com.raymisupreme.mscatalogo.dto.PromocionPublicDTO;
import com.raymisupreme.mscatalogo.service.CatalogoPublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Endpoints publicos para la tienda web. No requieren login.
 */
@RestController
@RequestMapping("/api/catalogo/public")
public class CatalogoPublicController {

    private final CatalogoPublicService catalogoPublicService;

    public CatalogoPublicController(CatalogoPublicService catalogoPublicService) {
        this.catalogoPublicService = catalogoPublicService;
    }

    @GetMapping("/home")
    public ResponseEntity<HomePublicDTO> home() {
        return ResponseEntity.ok(catalogoPublicService.obtenerHome());
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoPublicDTO>> listarProductos(
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) String publico) {
        return ResponseEntity.ok(catalogoPublicService.listarProductos(idCategoria, publico));
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoPublicDTO> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoPublicService.obtenerProducto(id));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaPublicDTO>> listarCategorias() {
        return ResponseEntity.ok(catalogoPublicService.listarCategorias());
    }

    @GetMapping("/promociones")
    public ResponseEntity<List<PromocionPublicDTO>> listarPromociones() {
        return ResponseEntity.ok(catalogoPublicService.listarPromociones());
    }
}
