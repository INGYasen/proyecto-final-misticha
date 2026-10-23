package com.raymisupreme.mscatalogo.client;

import com.raymisupreme.mscatalogo.config.FeignSecurityConfig;
import com.raymisupreme.mscatalogo.dto.PrendaInventarioResponse;
import com.raymisupreme.mscatalogo.dto.CategoriaInventarioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Cliente Feign hacia ms-inventario.
 * name debe coincidir con spring.application.name de ms-inventario.
 */
@FeignClient(name = "ms-inventario", configuration = FeignSecurityConfig.class)
public interface InventarioClient {

    @GetMapping("/api/v1/prendas")
    List<PrendaInventarioResponse> listarPrendas();

    @GetMapping("/api/v1/prendas/public/{id}")
    PrendaInventarioResponse obtenerPrenda(@PathVariable("id") Long id);

    @GetMapping("/api/v1/prendas/public")
    List<PrendaInventarioResponse> listarPrendasPublicas();

    @GetMapping("/api/v1/categorias")
    List<CategoriaInventarioResponse> listarCategorias();
}
