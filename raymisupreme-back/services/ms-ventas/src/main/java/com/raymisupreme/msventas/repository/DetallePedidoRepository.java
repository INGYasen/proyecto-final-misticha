package com.raymisupreme.msventas.repository;

import com.raymisupreme.msventas.entity.DetallePedidoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedidoEntity, Long> {
    List<DetallePedidoEntity> findByPedido_IdPedido(Long idPedido);
}

