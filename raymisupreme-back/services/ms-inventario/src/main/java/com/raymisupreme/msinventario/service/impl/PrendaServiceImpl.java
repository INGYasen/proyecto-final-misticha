package com.raymisupreme.msinventario.service.impl;

import com.raymisupreme.msinventario.client.dto.LoteCompraResponse;
import com.raymisupreme.msinventario.dto.CategoriaResumenResponse;
import com.raymisupreme.msinventario.dto.PrendaDetalleCompletoResponse;
import com.raymisupreme.msinventario.dto.PrendaRequest;
import com.raymisupreme.msinventario.dto.PrendaResponse;
import com.raymisupreme.msinventario.dto.StockResponse;
import com.raymisupreme.msinventario.entity.CategoriaEntity;
import com.raymisupreme.msinventario.entity.PrendaEntity;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;
import com.raymisupreme.msinventario.mapper.PrendaMapper;
import com.raymisupreme.msinventario.repository.PrendaRepository;
import com.raymisupreme.msinventario.repository.CategoriaRepository;
import com.raymisupreme.msinventario.service.AcopioIntegrationService;
import com.raymisupreme.msinventario.service.PrendaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional
public class PrendaServiceImpl implements PrendaService {

    private final PrendaRepository prendaRepository;
    private final CategoriaRepository categoriaRepository;
    private final AcopioIntegrationService acopioIntegrationService;

    public PrendaServiceImpl(PrendaRepository prendaRepository,
                             CategoriaRepository categoriaRepository,
                             AcopioIntegrationService acopioIntegrationService) {
        this.prendaRepository = prendaRepository;
        this.categoriaRepository = categoriaRepository;
        this.acopioIntegrationService = acopioIntegrationService;
    }

    @Override
    public PrendaResponse create(PrendaRequest request) {
        if (request.getIdLote() != null) {
            LoteCompraResponse lote = acopioIntegrationService.obtenerLote(request.getIdLote());
            if (lote == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "ms-acopio no disponible: no se puede validar el lote de compra.");
            }
        }

        validarFotoDePrenda(request.getUrlFoto());
        CategoriaEntity categoria = obtenerCategoria(request.getIdCategoria());
        PrendaEntity entity = PrendaMapper.toEntity(request);
        entity.setCategoria(categoria);

        PrendaEntity saved = prendaRepository.save(entity);
        return PrendaMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrendaResponse> getAll(Long idCategoria, PublicoObjetivo publico) {
        return prendaRepository.findByFiltros(idCategoria, publico)
                .stream()
                .map(PrendaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PrendaResponse getById(Long id) {
        PrendaEntity entity = prendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + id));
        return PrendaMapper.toResponse(entity);
    }

    @Override
    public PrendaResponse update(Long id, PrendaRequest request) {
        PrendaEntity entity = prendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + id));

        validarLote(request.getIdLote());
        entity.setIdLote(request.getIdLote());
        entity.setCategoria(obtenerCategoria(request.getIdCategoria()));
        entity.setPublico(request.getPublico() != null ? request.getPublico() : PublicoObjetivo.UNISEX);
        entity.setCodigoUnico(request.getCodigoUnico());
        entity.setNombrePrenda(request.getNombrePrenda());
        entity.setTalla(request.getTalla());
        entity.setColor(request.getColor());
        entity.setMaterial(request.getMaterial());
        entity.setStock(request.getStock() != null ? request.getStock() : 0);
        validarFotoDePrenda(request.getUrlFoto());
        entity.setUrlFoto(request.getUrlFoto());
        return PrendaMapper.toResponse(prendaRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        PrendaEntity entity = prendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + id));
        try {
            prendaRepository.delete(entity);
            prendaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la prenda porque tiene inspecciones asociadas", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PrendaDetalleCompletoResponse getDetalleCompleto(Long id) {
        PrendaEntity prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + id));

        LoteCompraResponse lote = acopioIntegrationService.obtenerLote(prenda.getIdLote());
        if (lote == null) {
            return PrendaDetalleCompletoResponse.builder()
                    .idPrenda(prenda.getIdPrenda())
                    .nombrePrenda(prenda.getNombrePrenda())
                    .codigoUnico(prenda.getCodigoUnico())
                    .talla(prenda.getTalla())
                    .color(prenda.getColor())
                    .material(prenda.getMaterial())
                    .stock(prenda.getStock())
                    .categoria(toCategoriaResumen(prenda.getCategoria()))
                    .publico(prenda.getPublico())
                    .lote(null)
                    .mensaje("ms-acopio no disponible")
                    .build();
        }

        return PrendaDetalleCompletoResponse.builder()
                .idPrenda(prenda.getIdPrenda())
                .nombrePrenda(prenda.getNombrePrenda())
                .codigoUnico(prenda.getCodigoUnico())
                .talla(prenda.getTalla())
                .color(prenda.getColor())
                .material(prenda.getMaterial())
                .stock(prenda.getStock())
                .categoria(toCategoriaResumen(prenda.getCategoria()))
                .publico(prenda.getPublico())
                .lote(lote)
                .build();
    }

    private CategoriaResumenResponse toCategoriaResumen(CategoriaEntity categoria) {
        if (categoria == null) {
            return null;
        }

        return CategoriaResumenResponse.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStock(Long id) {
        PrendaEntity prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + id));
        return new StockResponse(prenda.getIdPrenda(), prenda.getStock());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrendaResponse> getByIdLote(Long idLote) {
        return prendaRepository.findByIdLote(idLote)
                .stream()
                .map(PrendaMapper::toResponse)
                .toList();
    }

    @Override
    public void descontarStock(Long id, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad a descontar debe ser mayor a cero");
        }

        PrendaEntity prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenda no encontrada con id: " + id));

        int stockActual = prenda.getStock() != null ? prenda.getStock() : 0;
        if (stockActual < cantidad) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para la prenda " + id + ". Stock actual: " + stockActual);
        }

        prenda.setStock(stockActual - cantidad);
        prendaRepository.save(prenda);
    }

    private void validarFotoDePrenda(String urlFoto) {
        if (urlFoto != null && urlFoto.toLowerCase(java.util.Locale.ROOT).contains("carrusel")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La foto del carrusel es de campaña. Usa la foto de la prenda sola.");
        }
    }

    private CategoriaEntity obtenerCategoria(Long idCategoria) {
        if (idCategoria == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idCategoria es obligatorio");
        }
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Categoria no encontrada con id: " + idCategoria));
    }

    private void validarLote(Long idLote) {
        if (idLote == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idLote es obligatorio");
        }
        LoteCompraResponse lote = acopioIntegrationService.obtenerLote(idLote);
        if (lote == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "ms-acopio no disponible: no se puede validar el lote de compra.");
        }
    }
}
