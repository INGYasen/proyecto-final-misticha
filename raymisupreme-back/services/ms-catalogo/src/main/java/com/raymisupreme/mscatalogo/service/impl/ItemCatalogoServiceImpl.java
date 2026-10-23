package com.raymisupreme.mscatalogo.service.impl;

import com.raymisupreme.mscatalogo.dto.ItemCatalogoRequest;
import com.raymisupreme.mscatalogo.dto.ItemCatalogoResponse;
import com.raymisupreme.mscatalogo.entity.ItemCatalogoEntity;
import com.raymisupreme.mscatalogo.mapper.ItemCatalogoMapper;
import com.raymisupreme.mscatalogo.repository.ItemCatalogoRepository;
import com.raymisupreme.mscatalogo.service.ItemCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemCatalogoServiceImpl implements ItemCatalogoService {

    @Autowired
    private ItemCatalogoRepository repository;

    @Autowired
    private ItemCatalogoMapper mapper;

    @Override
    public ItemCatalogoResponse guardar(ItemCatalogoRequest request) {
        if (repository.existsByIdPrenda(request.getIdPrenda())) {
            throw new RuntimeException("Ya existe un item de catalogo para la prenda ID: " + request.getIdPrenda());
        }
        validarFotoDePrenda(request.getUrlFoto());
        ItemCatalogoEntity entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<ItemCatalogoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemCatalogoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Item de catalogo no encontrado con ID: " + id));
    }

    @Override
    public ItemCatalogoResponse actualizar(Long id, ItemCatalogoRequest request) {
        ItemCatalogoEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de catalogo no encontrado con ID: " + id));
        if (!entity.getIdPrenda().equals(request.getIdPrenda())
                && repository.existsByIdPrenda(request.getIdPrenda())) {
            throw new RuntimeException("Ya existe un item de catalogo para la prenda ID: " + request.getIdPrenda());
        }
        entity.setIdPrenda(request.getIdPrenda());
        validarFotoDePrenda(request.getUrlFoto());
        entity.setUrlFoto(request.getUrlFoto());
        entity.setDescripcionPublica(request.getDescripcionPublica());
        entity.setDestacado(Boolean.TRUE.equals(request.getDestacado()));
        entity.setActivo(request.getActivo() == null || request.getActivo());
        return mapper.toResponse(repository.save(entity));
    }

    private void validarFotoDePrenda(String urlFoto) {
        if (urlFoto != null && urlFoto.toLowerCase(java.util.Locale.ROOT).contains("carrusel")) {
            throw new IllegalArgumentException("La foto del carrusel es de campaña. Usa la foto de la prenda sola.");
        }
    }

    @Override
    public void eliminar(Long id) {
        ItemCatalogoEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de catalogo no encontrado con ID: " + id));
        repository.delete(entity);
    }
}
