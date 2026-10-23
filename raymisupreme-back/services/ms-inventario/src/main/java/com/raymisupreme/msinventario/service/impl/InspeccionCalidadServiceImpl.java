package com.raymisupreme.msinventario.service.impl;

import com.raymisupreme.msinventario.dto.InspeccionCalidadRequest;
import com.raymisupreme.msinventario.dto.InspeccionCalidadResponse;
import com.raymisupreme.msinventario.entity.InspeccionCalidadEntity;
import com.raymisupreme.msinventario.mapper.InspeccionCalidadMapper;
import com.raymisupreme.msinventario.repository.InspeccionCalidadRepository;
import com.raymisupreme.msinventario.repository.PrendaRepository;
import com.raymisupreme.msinventario.service.InspeccionCalidadService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InspeccionCalidadServiceImpl implements InspeccionCalidadService {

    private final InspeccionCalidadRepository inspeccionCalidadRepository;
    private final PrendaRepository prendaRepository;

    public InspeccionCalidadServiceImpl(InspeccionCalidadRepository inspeccionCalidadRepository,
                                        PrendaRepository prendaRepository) {
        this.inspeccionCalidadRepository = inspeccionCalidadRepository;
        this.prendaRepository = prendaRepository;
    }

    @Override
    public InspeccionCalidadResponse create(InspeccionCalidadRequest request) {
        InspeccionCalidadEntity entity = InspeccionCalidadMapper.toEntity(request);
        InspeccionCalidadEntity saved = inspeccionCalidadRepository.save(entity);
        return InspeccionCalidadMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspeccionCalidadResponse> getAll() {
        return inspeccionCalidadRepository.findAll()
                .stream()
                .map(InspeccionCalidadMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InspeccionCalidadResponse getById(Long id) {
        InspeccionCalidadEntity entity = inspeccionCalidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("InspeccionCalidad no encontrada con id: " + id));
        return InspeccionCalidadMapper.toResponse(entity);
    }

    @Override
    public InspeccionCalidadResponse update(Long id, InspeccionCalidadRequest request) {
        InspeccionCalidadEntity entity = inspeccionCalidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("InspeccionCalidad no encontrada con id: " + id));
        var prenda = prendaRepository.findById(request.getIdPrenda())
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + request.getIdPrenda()));
        entity.setPrenda(prenda);
        entity.setFechaInspeccion(request.getFechaInspeccion());
        entity.setResultado(request.getResultado());
        entity.setObservaciones(request.getObservaciones());
        return InspeccionCalidadMapper.toResponse(inspeccionCalidadRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        InspeccionCalidadEntity entity = inspeccionCalidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("InspeccionCalidad no encontrada con id: " + id));
        inspeccionCalidadRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspeccionCalidadResponse> getByIdPrenda(Long idPrenda) {
        return inspeccionCalidadRepository.findByPrenda_IdPrenda(idPrenda)
                .stream()
                .map(InspeccionCalidadMapper::toResponse)
                .toList();
    }
}
