package com.raymisupreme.mspagos.service;

import com.raymisupreme.mspagos.dto.StripeCheckoutResponse;
import com.raymisupreme.mspagos.dto.StripeSessionResponse;

public interface StripeCheckoutService {
    StripeCheckoutResponse crearSesion(Long ventaId);
    StripeSessionResponse consultarSesion(String sessionId);
}
