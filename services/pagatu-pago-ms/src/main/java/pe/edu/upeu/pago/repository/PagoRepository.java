package pe.edu.upeu.pago.repository;

import pe.edu.upeu.pago.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    boolean existsByCodigo(String value);
}
