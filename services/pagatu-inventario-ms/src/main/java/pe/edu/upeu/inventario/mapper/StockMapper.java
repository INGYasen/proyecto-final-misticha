package pe.edu.upeu.inventario.mapper;

import pe.edu.upeu.inventario.dto.StockRequest;
import pe.edu.upeu.inventario.dto.StockResponse;
import pe.edu.upeu.inventario.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public Stock toEntity(StockRequest request) {
        return Stock.builder()
                .idProducto(request.getIdProducto())
                .sku(request.getSku())
                .cantidad(request.getCantidad())
                .minimo(request.getMinimo())
                .almacen(request.getAlmacen())
                .build();
    }

    public StockResponse toResponse(Stock stock) {
        return StockResponse.builder()
                .id(stock.getId())
                .idProducto(stock.getIdProducto())
                .sku(stock.getSku())
                .cantidad(stock.getCantidad())
                .minimo(stock.getMinimo())
                .almacen(stock.getAlmacen())
                .build();
    }
}
