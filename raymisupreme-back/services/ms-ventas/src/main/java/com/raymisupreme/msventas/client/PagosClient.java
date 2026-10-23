package com.raymisupreme.msventas.client;

import com.raymisupreme.msventas.config.FeignSecurityConfig;
import com.raymisupreme.msventas.dto.PagoRequest;
import com.raymisupreme.msventas.dto.PagoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente Feign hacia ms-pagos.
 * name debe coincidir con spring.application.name de ms-pagos.
 */
@FeignClient(name = "ms-pagos", configuration = FeignSecurityConfig.class)
public interface PagosClient {

    @PostMapping("/api/v1/pagos/procesar")
    PagoResponse procesar(@RequestBody PagoRequest request);
}
