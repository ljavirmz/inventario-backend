package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.model.Categoria;
import com.serviciosocial.inventario_backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }
    
    public List<Categoria> obtenerActivas() {
        return categoriaRepository.findAll().stream()
            .filter(Categoria::getActivo)
            .toList();
    }
    
    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }
    
    public Categoria crear(Categoria categoria) {
        categoria.setActivo(true);
        return categoriaRepository.save(categoria);
    }
    
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria categoria = obtenerPorId(id);
        categoria.setNombre(categoriaActualizada.getNombre());
        return categoriaRepository.save(categoria);
    }
    
    public void desactivar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }
    
    public void activar(Long id) {
        Categoria categoria = obtenerPorId(id);
        categoria.setActivo(true);
        categoriaRepository.save(categoria);
    }
}