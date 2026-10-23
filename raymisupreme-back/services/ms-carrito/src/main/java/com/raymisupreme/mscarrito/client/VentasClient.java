package com.raymisupreme.mscarrito.client;

import com.raymisupreme.mscarrito.client.dto.ClienteResponse;
import com.raymisupreme.mscarrito.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-ventas", configuration = FeignSecurityConfig.class)
public interface VentasClient {

    @GetMapping("/api/v1/clientes/{idCliente}")
    ClienteResponse obtenerCliente(@PathVariable("idCliente") Long idCliente);
}
