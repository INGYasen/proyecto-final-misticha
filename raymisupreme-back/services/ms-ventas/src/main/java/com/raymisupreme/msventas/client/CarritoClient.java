package com.raymisupreme.msventas.client;

import com.raymisupreme.msventas.config.FeignSecurityConfig;
import com.raymisupreme.msventas.dto.CarritoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-carrito", configuration = FeignSecurityConfig.class)
public interface CarritoClient {

    @GetMapping("/api/v1/carrito/{idCliente}")
    CarritoResponse obtenerCarrito(@PathVariable("idCliente") Long idCliente);

    @DeleteMapping("/api/v1/carrito/{clienteId}")
    void limpiarCarrito(@PathVariable("clienteId") Long clienteId);
}
