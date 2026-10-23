package com.raymisupreme.mspagos.repository;

import com.raymisupreme.mspagos.entity.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long> {

    Optional<ComprobanteEntity> findByPago_Id(Long pagoId);
}
