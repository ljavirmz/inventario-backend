package com.serviciosocial.inventario_backend.repository;

import com.serviciosocial.inventario_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}