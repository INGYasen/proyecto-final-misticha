package com.raymisupreme.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/ms-acopio")
    public ResponseEntity<Map<String, String>> fallbackAcopio() {
        return unavailable("ms-acopio no disponible",
                "ms-acopio caido: no se pueden consultar artesanos ni lotes temporalmente.");
    }

    @RequestMapping("/ms-inventario")
    public ResponseEntity<Map<String, String>> fallbackInventario() {
        return unavailable("ms-inventario no disponible",
                "ms-inventario caido: no se pueden consultar prendas, stock ni calidad temporalmente.");
    }

    @RequestMapping("/ms-ventas")
    public ResponseEntity<Map<String, String>> fallbackVentas() {
        return unavailable("ms-ventas no disponible",
                "ms-ventas caido: pedidos, clientes y confirmacion de compra estan temporalmente fuera de servicio.");
    }

    @RequestMapping("/ms-catalogo")
    public ResponseEntity<Map<String, String>> fallbackCatalogo() {
        return unavailable("ms-catalogo no disponible",
                "ms-catalogo caido: el catalogo y los precios estan temporalmente fuera de servicio.");
    }

    @RequestMapping("/ms-pagos")
    public ResponseEntity<Map<String, String>> fallbackPagos() {
        return unavailable("ms-pagos no disponible",
                "ms-pagos caido: no se realizo ningun cobro.");
    }

    @RequestMapping("/ms-carrito")
    public ResponseEntity<Map<String, String>> fallbackCarrito() {
        return unavailable("ms-carrito no disponible",
                "ms-carrito caido: el carrito esta temporalmente fuera de servicio.");
    }

    @RequestMapping("/ms-auth")
    public ResponseEntity<Map<String, String>> fallbackAuth() {
        return unavailable("ms-auth no disponible",
                "ms-auth caido: la gestion de usuarios esta temporalmente fuera de servicio.");
    }

    @RequestMapping("/ms-promociones")
    public ResponseEntity<Map<String, String>> fallbackPromociones() {
        return unavailable("ms-promociones no disponible",
                "ms-promociones caido: las ofertas automaticas estan temporalmente fuera de servicio. El resto de la tienda sigue disponible.");
    }

    private ResponseEntity<Map<String, String>> unavailable(String error, String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", error, "mensaje", message));
    }
}
