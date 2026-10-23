package com.raymisupreme.msventas.client;

import com.raymisupreme.msventas.config.FeignSecurityConfig;
import com.raymisupreme.msventas.dto.PrendaDto;
import com.raymisupreme.msventas.dto.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Cliente Feign para comunicarse con ms-inventario.
 * Permite que ms-ventas consulte los datos de una prenda
 * (precio, stock, etc.) al registrar un detalle de pedido.
 *
 * El name="ms-inventario" debe coincidir exactamente con
 * spring.application.name en ms-inventario.
 */
@FeignClient(name = "ms-inventario", configuration = FeignSecurityConfig.class)
public interface InventarioClient {

    @GetMapping("/api/v1/prendas/{id}")
    PrendaDto findPrendaById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/prendas/{id}/stock")
    StockResponse obtenerStock(@PathVariable("id") Long id,
            @RequestHeader("X-Internal-Payment-Secret") String internalSecret);

    @PutMapping("/api/v1/prendas/{id}/descontar")
    void descontarStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad,
            @RequestHeader("X-Internal-Payment-Secret") String internalSecret);
}
