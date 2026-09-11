package pe.edu.upeu.inventario.repository;

import pe.edu.upeu.inventario.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    boolean existsBySku(String sku);
    boolean existsByIdProducto(Long idProducto);
}
