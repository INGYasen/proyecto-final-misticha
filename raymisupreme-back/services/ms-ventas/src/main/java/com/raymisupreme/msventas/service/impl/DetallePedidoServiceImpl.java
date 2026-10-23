package com.raymisupreme.msventas.service.impl;

import com.raymisupreme.msventas.dto.DetallePedidoRequest;
import com.raymisupreme.msventas.dto.DetallePedidoResponse;
import com.raymisupreme.msventas.entity.DetallePedidoEntity;
import com.raymisupreme.msventas.mapper.DetallePedidoMapper;
import com.raymisupreme.msventas.repository.DetallePedidoRepository;
import com.raymisupreme.msventas.service.DetallePedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    public DetallePedidoResponse crear(DetallePedidoRequest request) {
        validarPrecioUnitario(request);
        DetallePedidoEntity entity = DetallePedidoMapper.toEntity(request);
        DetallePedidoEntity guardado = detallePedidoRepository.save(entity);
        return DetallePedidoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public DetallePedidoResponse obtenerPorId(Long idDetalle) {
        DetallePedidoEntity entity = detallePedidoRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + idDetalle));
        return DetallePedidoMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> listar() {
        return detallePedidoRepository.findAll().stream()
                .map(DetallePedidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> listarPorPedido(Long idPedido) {
        return detallePedidoRepository.findByPedido_IdPedido(idPedido).stream()
                .map(DetallePedidoMapper::toResponse)
                .toList();
    }

    @Override
    public DetallePedidoResponse actualizar(Long idDetalle, DetallePedidoRequest request) {
        validarPrecioUnitario(request);
        DetallePedidoEntity entity = detallePedidoRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + idDetalle));
        DetallePedidoMapper.copyToEntity(request, entity);
        DetallePedidoEntity actualizado = detallePedidoRepository.save(entity);
        return DetallePedidoMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Long idDetalle) {
        if (!detallePedidoRepository.existsById(idDetalle)) {
            throw new IllegalArgumentException("Detalle no encontrado: " + idDetalle);
        }
        detallePedidoRepository.deleteById(idDetalle);
    }

    private void validarPrecioUnitario(DetallePedidoRequest request) {
        if (request == null || request.getPrecioUnitario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "precioUnitario es obligatorio");
        }
    }
}
