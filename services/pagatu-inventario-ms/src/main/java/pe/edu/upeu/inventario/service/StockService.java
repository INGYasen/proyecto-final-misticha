package pe.edu.upeu.inventario.service;

import pe.edu.upeu.inventario.dto.StockRequest;
import pe.edu.upeu.inventario.dto.StockResponse;
import pe.edu.upeu.inventario.entity.Stock;
import pe.edu.upeu.inventario.exception.DuplicateResourceException;
import pe.edu.upeu.inventario.exception.ResourceNotFoundException;
import pe.edu.upeu.inventario.mapper.StockMapper;
import pe.edu.upeu.inventario.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public List<StockResponse> listar() {
        return stockRepository.findAll().stream().map(stockMapper::toResponse).toList();
    }

    public StockResponse obtener(Long id) {
        return stockMapper.toResponse(buscarOFallar(id));
    }

    public StockResponse crear(StockRequest request) {
        if (stockRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Ya existe stock con sku " + request.getSku());
        }
        if (stockRepository.existsByIdProducto(request.getIdProducto())) {
            throw new DuplicateResourceException("Ya existe stock para el producto " + request.getIdProducto());
        }
        return stockMapper.toResponse(stockRepository.save(stockMapper.toEntity(request)));
    }

    public StockResponse actualizar(Long id, StockRequest request) {
        Stock stock = buscarOFallar(id);
        stock.setIdProducto(request.getIdProducto());
        stock.setSku(request.getSku());
        stock.setCantidad(request.getCantidad());
        stock.setMinimo(request.getMinimo());
        stock.setAlmacen(request.getAlmacen());
        return stockMapper.toResponse(stockRepository.save(stock));
    }

    public void eliminar(Long id) {
        stockRepository.delete(buscarOFallar(id));
    }

    private Stock buscarOFallar(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado: " + id));
    }
}
