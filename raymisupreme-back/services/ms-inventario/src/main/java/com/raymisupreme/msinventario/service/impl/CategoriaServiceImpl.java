package com.raymisupreme.msinventario.service.impl;

import com.raymisupreme.msinventario.dto.CategoriaRequest;
import com.raymisupreme.msinventario.dto.CategoriaResponse;
import com.raymisupreme.msinventario.entity.CategoriaEntity;
import com.raymisupreme.msinventario.mapper.CategoriaMapper;
import com.raymisupreme.msinventario.repository.CategoriaRepository;
import com.raymisupreme.msinventario.service.CategoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public CategoriaResponse create(CategoriaRequest request) {
        CategoriaEntity entity = CategoriaMapper.toEntity(request);
        CategoriaEntity saved = categoriaRepository.save(entity);
        return CategoriaMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> getAll() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse getById(Long id) {
        CategoriaEntity entity = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada con id: " + id));
        return CategoriaMapper.toResponse(entity);
    }

    @Override
    public CategoriaResponse update(Long id, CategoriaRequest request) {
        CategoriaEntity entity = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada con id: " + id));
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return CategoriaMapper.toResponse(categoriaRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        CategoriaEntity entity = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada con id: " + id));
        try {
            categoriaRepository.delete(entity);
            categoriaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la categoria porque tiene prendas asociadas", ex);
        }
    }
}
