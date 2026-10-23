package com.raymisupreme.msventas.service;

import com.raymisupreme.msventas.client.PagosClient;
import com.raymisupreme.msventas.dto.PagoRequest;
import com.raymisupreme.msventas.dto.PagoResponse;
import com.raymisupreme.msventas.exception.MicroserviceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PagosIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(PagosIntegrationService.class);

    private final PagosClient pagosClient;

    public PagosIntegrationService(PagosClient pagosClient) {
        this.pagosClient = pagosClient;
    }

    @CircuitBreaker(name = "ms-pagos", fallbackMethod = "procesarPagoFallback")
    public PagoResponse procesarPago(PagoRequest request) {
        return pagosClient.procesar(request);
    }

    public PagoResponse procesarPagoFallback(PagoRequest request, Throwable ex) {
        log.warn("ms-pagos no disponible para pedido {}: {}", request.getPedidoId(), ex.getMessage());
        throw new MicroserviceUnavailableException("ms-pagos no disponible");
    }
}
