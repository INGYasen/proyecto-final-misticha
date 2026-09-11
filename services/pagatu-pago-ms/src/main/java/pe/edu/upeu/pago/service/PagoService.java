package pe.edu.upeu.pago.service;

import pe.edu.upeu.pago.dto.PagoRequest;
import pe.edu.upeu.pago.dto.PagoResponse;
import pe.edu.upeu.pago.entity.Pago;
import pe.edu.upeu.pago.exception.ResourceNotFoundException;
import pe.edu.upeu.pago.exception.DuplicateResourceException;
import pe.edu.upeu.pago.mapper.PagoMapper;
import pe.edu.upeu.pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;

    public List<PagoResponse> listar() {
        return pagoRepository.findAll().stream().map(pagoMapper::toResponse).toList();
    }

    public PagoResponse obtener(Long id) {
        return pagoMapper.toResponse(buscarOFallar(id));
    }

    public PagoResponse crear(PagoRequest request) {

        if (pagoRepository.existsByCodigo(request.getCodigo())) {
            throw new DuplicateResourceException("Pago duplicado: " + request.getCodigo());
        }
        return pagoMapper.toResponse(pagoRepository.save(pagoMapper.toEntity(request)));
    }

    public PagoResponse actualizar(Long id, PagoRequest request) {
        Pago entity = buscarOFallar(id);
        entity.setCodigo(request.getCodigo());
        entity.setOrdenId(request.getOrdenId());
        entity.setMonto(request.getMonto());
        entity.setEstado(request.getEstado());
        entity.setMetodo(request.getMetodo());
        return pagoMapper.toResponse(pagoRepository.save(entity));
    }

    public void eliminar(Long id) {
        pagoRepository.delete(buscarOFallar(id));
    }

    private Pago buscarOFallar(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    }
}
