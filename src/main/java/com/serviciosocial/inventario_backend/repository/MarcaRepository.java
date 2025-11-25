package com.serviciosocial.inventario_backend.repository;

import com.serviciosocial.inventario_backend.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
}