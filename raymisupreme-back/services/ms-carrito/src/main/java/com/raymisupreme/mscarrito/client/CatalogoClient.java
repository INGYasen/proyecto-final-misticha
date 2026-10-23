package com.raymisupreme.mscarrito.client;

import com.raymisupreme.mscarrito.client.dto.ProductoCatalogoResponse;
import com.raymisupreme.mscarrito.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-catalogo", configuration = FeignSecurityConfig.class)
public interface CatalogoClient {

    @GetMapping("/api/v1/catalogo/productos/{idPrenda}")
    ProductoCatalogoResponse obtenerProducto(@PathVariable("idPrenda") Long idPrenda);
}
