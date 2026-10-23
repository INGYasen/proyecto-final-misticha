package com.raymisupreme.mspagos.client;

import com.raymisupreme.mspagos.config.FeignSecurityConfig;
import com.raymisupreme.mspagos.dto.ventas.DetallePedidoVentasResponse;
import com.raymisupreme.mspagos.dto.ventas.PedidoVentasResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ms-ventas", configuration = FeignSecurityConfig.class)
public interface VentasClient {
    @GetMapping("/api/v1/pedidos/{pedidoId}")
    PedidoVentasResponse obtenerPedido(@PathVariable("pedidoId") Long pedidoId);

    @GetMapping("/api/v1/detalles")
    List<DetallePedidoVentasResponse> obtenerDetalles(@RequestParam("idPedido") Long pedidoId);

    @PostMapping("/api/v1/pedidos/{pedidoId}/confirmar-pago-interno")
    void confirmarPago(@PathVariable("pedidoId") Long pedidoId,
                       @RequestHeader("X-Internal-Payment-Secret") String internalSecret);
}
