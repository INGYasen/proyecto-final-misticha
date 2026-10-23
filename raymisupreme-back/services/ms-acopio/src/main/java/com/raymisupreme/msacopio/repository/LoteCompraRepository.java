package com.raymisupreme.msacopio.repository;

import com.raymisupreme.msacopio.entity.LoteCompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteCompraRepository extends JpaRepository<LoteCompraEntity, Long> {
}