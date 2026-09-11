package pe.edu.upeu.inventario.service;

import pe.edu.upeu.inventario.dto.MovimientoRequest;
import pe.edu.upeu.inventario.dto.MovimientoResponse;
import pe.edu.upeu.inventario.entity.Movimiento;
import pe.edu.upeu.inventario.entity.Stock;
import pe.edu.upeu.inventario.exception.ResourceNotFoundException;
import pe.edu.upeu.inventario.mapper.MovimientoMapper;
import pe.edu.upeu.inventario.repository.MovimientoRepository;
import pe.edu.upeu.inventario.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private static final Set<String> TIPOS = Set.of("INGRESO", "SALIDA", "AJUSTE");

    private final MovimientoRepository movimientoRepository;
    private final StockRepository stockRepository;
    private final MovimientoMapper movimientoMapper;

    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAll().stream().map(movimientoMapper::toResponse).toList();
    }

    public MovimientoResponse obtener(Long id) {
        return movimientoMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public MovimientoResponse crear(MovimientoRequest request) {
        String tipo = request.getTipo().toUpperCase();
        if (!TIPOS.contains(tipo)) {
            throw new IllegalArgumentException("Tipo invalido. Use INGRESO, SALIDA o AJUSTE");
        }
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado: " + request.getStockId()));
        if ("INGRESO".equals(tipo)) {
            stock.setCantidad(stock.getCantidad() + request.getCantidad());
        } else if ("SALIDA".equals(tipo)) {
            int nuevo = stock.getCantidad() - request.getCantidad();
            if (nuevo < 0) {
                throw new IllegalArgumentException("No hay stock suficiente para la salida");
            }
            stock.setCantidad(nuevo);
        } else {
            stock.setCantidad(request.getCantidad());
        }
        stockRepository.save(stock);
        Movimiento movimiento = Movimiento.builder()
                .stock(stock)
                .tipo(tipo)
                .cantidad(request.getCantidad())
                .motivo(request.getMotivo())
                .fecha(LocalDateTime.now())
                .build();
        return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
    }

    @Transactional
    public MovimientoResponse actualizar(Long id, MovimientoRequest request) {
        Movimiento movimiento = buscarOFallar(id);
        movimiento.setTipo(request.getTipo().toUpperCase());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo());
        return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
    }

    public void eliminar(Long id) {
        movimientoRepository.delete(buscarOFallar(id));
    }

    private Movimiento buscarOFallar(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id));
    }
}
