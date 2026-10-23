package com.raymisupreme.msventas.service;

import com.raymisupreme.msventas.client.InventarioClient;
import com.raymisupreme.msventas.dto.StockResponse;
import com.raymisupreme.msventas.exception.MicroserviceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class InventarioIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(InventarioIntegrationService.class);

    private final InventarioClient inventarioClient;
    private final String internalSecret;

    public InventarioIntegrationService(InventarioClient inventarioClient,
            @Value("${internal.payment-secret}") String internalSecret) {
        this.inventarioClient = inventarioClient;
        this.internalSecret = internalSecret;
    }

    @CircuitBreaker(name = "ms-inventario", fallbackMethod = "obtenerStockFallback")
    public StockResponse obtenerStock(Long idPrenda) {
        return inventarioClient.obtenerStock(idPrenda, internalSecret);
    }

    public StockResponse obtenerStockFallback(Long idPrenda, Throwable ex) {
        log.warn("ms-inventario no disponible al consultar stock de prenda {}: {}", idPrenda, ex.getMessage());
        throw new MicroserviceUnavailableException("ms-inventario no disponible");
    }

    @CircuitBreaker(name = "ms-inventario", fallbackMethod = "descontarStockFallback")
    public void descontarStock(Long idPrenda, Integer cantidad) {
        inventarioClient.descontarStock(idPrenda, cantidad, internalSecret);
    }

    public void descontarStockFallback(Long idPrenda, Integer cantidad, Throwable ex) {
        log.warn("ms-inventario no disponible al descontar stock de prenda {}: {}", idPrenda, ex.getMessage());
        throw new MicroserviceUnavailableException("ms-inventario no disponible");
    }
}
