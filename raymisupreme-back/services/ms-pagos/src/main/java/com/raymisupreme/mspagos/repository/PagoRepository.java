package com.raymisupreme.mspagos.repository;

import com.raymisupreme.mspagos.entity.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Collection;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    Optional<PagoEntity> findByPedidoId(Long pedidoId);

    Optional<PagoEntity> findByPreferenceId(String preferenceId);

    Optional<PagoEntity> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PagoEntity> findWithLockByMercadoPagoPaymentId(String mercadoPagoPaymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PagoEntity> findWithLockByPedidoId(Long pedidoId);

    Optional<PagoEntity> findTopByPedidoIdAndEstadoInOrderByIdDesc(
            Long pedidoId, Collection<String> estados);

    Optional<PagoEntity> findByStripeSessionId(String stripeSessionId);
}
