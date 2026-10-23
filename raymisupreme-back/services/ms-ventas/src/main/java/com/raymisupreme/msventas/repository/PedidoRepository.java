package com.raymisupreme.msventas.repository;

import com.raymisupreme.msventas.entity.PedidoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    List<PedidoEntity> findByCliente_IdCliente(Long idCliente);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PedidoEntity> findWithLockByIdPedido(Long idPedido);
}
