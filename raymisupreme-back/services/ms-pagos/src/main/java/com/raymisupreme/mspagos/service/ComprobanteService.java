package com.raymisupreme.mspagos.service;

import com.raymisupreme.mspagos.dto.ComprobanteResponse;

public interface ComprobanteService {

    ComprobanteResponse obtenerPorPagoId(Long pagoId);
}
