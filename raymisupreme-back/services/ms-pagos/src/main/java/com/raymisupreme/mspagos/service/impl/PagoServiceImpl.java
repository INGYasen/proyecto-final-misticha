package com.raymisupreme.mspagos.service.impl;

import com.raymisupreme.mspagos.dto.PagoEstadoRequest;
import com.raymisupreme.mspagos.dto.PagoRequest;
import com.raymisupreme.mspagos.dto.PagoResponse;
import com.raymisupreme.mspagos.entity.ComprobanteEntity;
import com.raymisupreme.mspagos.entity.PagoEntity;
import com.raymisupreme.mspagos.mapper.PagoMapper;
import com.raymisupreme.mspagos.repository.ComprobanteRepository;
import com.raymisupreme.mspagos.repository.PagoRepository;
import com.raymisupreme.mspagos.service.PagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final PagoMapper pagoMapper;

    public PagoServiceImpl(PagoRepository pagoRepository,
                           ComprobanteRepository comprobanteRepository,
                           PagoMapper pagoMapper) {
        this.pagoRepository = pagoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.pagoMapper = pagoMapper;
    }

    @Override
    @Transactional
    public PagoResponse procesarPago(PagoRequest request) {
        LocalDateTime ahora = LocalDateTime.now();

        PagoEntity pago = PagoEntity.builder()
                .pedidoId(request.getPedidoId())
                .clienteId(request.getClienteId())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago().trim().toUpperCase())
                .estado("PENDIENTE")
                .fechaPago(ahora)
                .creadoEn(ahora)
                .actualizadoEn(ahora)
                .build();

        boolean exitoso = esPagoValido(request.getMonto(), request.getMetodoPago());
        if (exitoso) {
            pago.setEstado("EXITOSO");
            pago.setReferenciaPago("REF-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        } else {
            pago.setEstado("FALLIDO");
        }
        pago.setActualizadoEn(LocalDateTime.now());

        PagoEntity guardado = pagoRepository.save(pago);
        PagoResponse response = pagoMapper.toResponse(guardado);

        if ("EXITOSO".equals(guardado.getEstado())) {
            ComprobanteEntity comprobante = ComprobanteEntity.builder()
                    .pago(guardado)
                    .numeroComprobante(generarNumeroComprobante())
                    .tipo("BOLETA")
                    .fechaEmision(LocalDateTime.now())
                    .montoTotal(guardado.getMonto())
                    .activo(true)
                    .build();
            comprobanteRepository.save(comprobante);
            response.setNumeroComprobante(comprobante.getNumeroComprobante());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse obtenerPorId(Long id) {
        PagoEntity pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        return enriquecerConComprobante(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse obtenerPorPedidoId(Long pedidoId) {
        PagoEntity pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para pedido ID: " + pedidoId));
        return enriquecerConComprobante(pago);
    }

    @Override
    @Transactional
    public PagoResponse actualizarEstado(Long id, PagoEstadoRequest request) {
        PagoEntity pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        pago.setEstado(request.getEstado().trim().toUpperCase());
        pago.setActualizadoEn(LocalDateTime.now());
        return enriquecerConComprobante(pagoRepository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(this::enriquecerConComprobante)
                .collect(Collectors.toList());
    }

    private PagoResponse enriquecerConComprobante(PagoEntity pago) {
        PagoResponse response = pagoMapper.toResponse(pago);
        comprobanteRepository.findByPago_Id(pago.getId())
                .ifPresent(c -> response.setNumeroComprobante(c.getNumeroComprobante()));
        return response;
    }

    private boolean esPagoValido(BigDecimal monto, String metodoPago) {
        return monto != null
                && monto.compareTo(BigDecimal.ZERO) > 0
                && metodoPago != null
                && !metodoPago.isBlank();
    }

    private String generarNumeroComprobante() {
        return "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
