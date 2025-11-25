package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.model.Marca;
import com.serviciosocial.inventario_backend.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MarcaService {
    
    @Autowired
    private MarcaRepository marcaRepository;
    
    public List<Marca> obtenerTodas() {
        return marcaRepository.findAll();
    }
    
    public List<Marca> obtenerActivas() {
        return marcaRepository.findAll().stream()
            .filter(Marca::getActivo)
            .toList();
    }
    
    public Marca obtenerPorId(Long id) {
        return marcaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
    }
    
    public Marca crear(Marca marca) {
        marca.setActivo(true);
        return marcaRepository.save(marca);
    }
    
    public Marca actualizar(Long id, Marca marcaActualizada) {
        Marca marca = obtenerPorId(id);
        marca.setNombre(marcaActualizada.getNombre());
        return marcaRepository.save(marca);
    }
    
    public void desactivar(Long id) {
        Marca marca = obtenerPorId(id);
        marca.setActivo(false);
        marcaRepository.save(marca);
    }
    
    public void activar(Long id) {
        Marca marca = obtenerPorId(id);
        marca.setActivo(true);
        marcaRepository.save(marca);
    }
}