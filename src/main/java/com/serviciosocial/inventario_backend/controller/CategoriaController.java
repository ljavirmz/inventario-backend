package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.model.Categoria;
import com.serviciosocial.inventario_backend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodas() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Categoria>> obtenerActivas() {
        return ResponseEntity.ok(categoriaService.obtenerActivas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.crear(categoria));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.actualizar(id, categoria));
    }
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoriaService.desactivar(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        categoriaService.activar(id);
        return ResponseEntity.ok().build();
    }
}