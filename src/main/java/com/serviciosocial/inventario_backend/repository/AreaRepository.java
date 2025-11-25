package com.serviciosocial.inventario_backend.repository;

import com.serviciosocial.inventario_backend.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
}