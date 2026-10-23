package com.raymisupreme.msacopio.repository;

import com.raymisupreme.msacopio.entity.ArtesanoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtesanoRepository extends JpaRepository<ArtesanoEntity, Long> {
}