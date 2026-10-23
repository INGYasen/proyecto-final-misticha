package com.raymisupreme.msinventario.service;

import com.raymisupreme.msinventario.client.AcopioClient;
import com.raymisupreme.msinventario.client.dto.LoteCompraResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AcopioIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(AcopioIntegrationService.class);

    private final AcopioClient acopioClient;

    public AcopioIntegrationService(AcopioClient acopioClient) {
        this.acopioClient = acopioClient;
    }

    @CircuitBreaker(name = "ms-acopio", fallbackMethod = "obtenerLoteFallback")
    public LoteCompraResponse obtenerLote(Long idLote) {
        return acopioClient.obtenerLote(idLote);
    }

    public LoteCompraResponse obtenerLoteFallback(Long idLote, Throwable ex) {
        log.warn("ms-acopio no disponible al consultar lote {}: {}", idLote, ex.getMessage());
        return null;
    }
}
