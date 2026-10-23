package com.raymisupreme.mspagos.service;

import com.raymisupreme.mspagos.dto.MercadoPagoPreferenciaResponse;
import com.raymisupreme.mspagos.dto.MercadoPagoPaymentRequest;
import com.raymisupreme.mspagos.dto.MercadoPagoPaymentResponse;
import com.raymisupreme.mspagos.dto.PagoResponse;
import java.util.Map;

public interface MercadoPagoService {
    MercadoPagoPreferenciaResponse crearPreferencia(Long pedidoId);
    MercadoPagoPaymentResponse crearPago(MercadoPagoPaymentRequest request);
    PagoResponse consultarPagoPropio(Long pedidoId);
    boolean firmaWebhookValida(String dataId, String requestId, String signature);
    void procesarWebhook(String type, String topic, String id, Map<String, Object> payload);
}
