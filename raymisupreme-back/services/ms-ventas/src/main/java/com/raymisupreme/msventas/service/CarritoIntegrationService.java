package com.raymisupreme.msventas.service;

import com.raymisupreme.msventas.client.CarritoClient;
import com.raymisupreme.msventas.dto.CarritoResponse;
import com.raymisupreme.msventas.exception.MicroserviceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CarritoIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(CarritoIntegrationService.class);

    private final CarritoClient carritoClient;

    public CarritoIntegrationService(CarritoClient carritoClient) {
        this.carritoClient = carritoClient;
    }

    @CircuitBreaker(name = "ms-carrito", fallbackMethod = "obtenerCarritoFallback")
    public CarritoResponse obtenerCarrito(Long idCliente) {
        return carritoClient.obtenerCarrito(idCliente);
    }

    public CarritoResponse obtenerCarritoFallback(Long idCliente, Throwable ex) {
        log.warn("ms-carrito no disponible para cliente {}: {}", idCliente, ex.getMessage());
        throw new MicroserviceUnavailableException("ms-carrito no disponible");
    }

    @CircuitBreaker(name = "ms-carrito", fallbackMethod = "limpiarCarritoFallback")
    public void limpiarCarrito(Long idCliente) {
        carritoClient.limpiarCarrito(idCliente);
    }

    public void limpiarCarritoFallback(Long idCliente, Throwable ex) {
        log.warn("ms-carrito no disponible al limpiar carrito del cliente {}: {}", idCliente, ex.getMessage());
        throw new MicroserviceUnavailableException("ms-carrito no disponible");
    }
}
