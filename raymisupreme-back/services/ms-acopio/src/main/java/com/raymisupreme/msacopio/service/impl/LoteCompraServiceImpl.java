package com.raymisupreme.msacopio.service.impl;

import com.raymisupreme.msacopio.dto.LoteCompraRequest;
import com.raymisupreme.msacopio.dto.LoteCompraResponse;
import com.raymisupreme.msacopio.entity.LoteCompraEntity;
import com.raymisupreme.msacopio.mapper.LoteCompraMapper;
import com.raymisupreme.msacopio.repository.ArtesanoRepository;
import com.raymisupreme.msacopio.repository.LoteCompraRepository;
import com.raymisupreme.msacopio.service.LoteCompraService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LoteCompraServiceImpl implements LoteCompraService {

    @Autowired
    private LoteCompraRepository loteRepository;
    @Autowired
    private ArtesanoRepository artesanoRepository;
    @Autowired
    private LoteCompraMapper mapper;

    @Override
    @Transactional
    public LoteCompraResponse registrarLote(LoteCompraRequest request) {
        // Buscamos al artesano para asegurar que existe antes de vincular el lote
        var artesano = artesanoRepository.findById(request.getIdArtesano())
                .orElseThrow(() -> new RuntimeException("Artesano no encontrado"));

        LoteCompraEntity entity = mapper.toEntity(request);
        entity.setArtesano(artesano);

        return mapper.toResponse(loteRepository.save(entity));
    }

    @Override
    public List<LoteCompraResponse> listarTodos() {
        return loteRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LoteCompraResponse buscarPorId(Long id) {
        return loteRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    @Override
    @Transactional
    public LoteCompraResponse actualizar(Long id, LoteCompraRequest request) {
        LoteCompraEntity entity = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado con ID: " + id));
        var artesano = artesanoRepository.findById(request.getIdArtesano())
                .orElseThrow(() -> new EntityNotFoundException("Artesano no encontrado con ID: " + request.getIdArtesano()));
        entity.setArtesano(artesano);
        entity.setFechaCompra(request.getFechaCompra());
        entity.setPrecioTotalPagado(request.getPrecioTotalPagado());
        entity.setCantidadTotalRecibida(request.getCantidadTotalRecibida());
        entity.setDescripcionGeneral(request.getDescripcionGeneral());
        entity.setEstadoLote(request.getEstadoLote());
        return mapper.toResponse(loteRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        LoteCompraEntity entity = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado con ID: " + id));
        loteRepository.delete(entity);
    }
}
