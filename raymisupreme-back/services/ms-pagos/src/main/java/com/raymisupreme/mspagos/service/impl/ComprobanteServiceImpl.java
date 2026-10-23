package com.raymisupreme.mspagos.service.impl;

import com.raymisupreme.mspagos.dto.ComprobanteResponse;
import com.raymisupreme.mspagos.entity.ComprobanteEntity;
import com.raymisupreme.mspagos.mapper.ComprobanteMapper;
import com.raymisupreme.mspagos.repository.ComprobanteRepository;
import com.raymisupreme.mspagos.service.ComprobanteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprobanteServiceImpl implements ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final ComprobanteMapper comprobanteMapper;

    public ComprobanteServiceImpl(ComprobanteRepository comprobanteRepository,
                                  ComprobanteMapper comprobanteMapper) {
        this.comprobanteRepository = comprobanteRepository;
        this.comprobanteMapper = comprobanteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ComprobanteResponse obtenerPorPagoId(Long pagoId) {
        ComprobanteEntity comprobante = comprobanteRepository.findByPago_Id(pagoId)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado para pago ID: " + pagoId));
        return comprobanteMapper.toResponse(comprobante);
    }
}
