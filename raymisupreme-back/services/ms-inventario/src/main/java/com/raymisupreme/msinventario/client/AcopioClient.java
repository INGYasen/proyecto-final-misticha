package com.raymisupreme.msinventario.client;

import com.raymisupreme.msinventario.client.dto.LoteCompraResponse;
import com.raymisupreme.msinventario.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-acopio", configuration = FeignSecurityConfig.class)
public interface AcopioClient {

    @GetMapping("/api/v1/lotes/{id}")
    LoteCompraResponse obtenerLote(@PathVariable("id") Long id);
}
