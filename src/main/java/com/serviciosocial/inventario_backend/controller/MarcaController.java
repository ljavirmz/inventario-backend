package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.model.Marca;
import com.serviciosocial.inventario_backend.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MarcaController {
    
    @Autowired
    private MarcaService marcaService;
    
    @GetMapping
    public ResponseEntity<List<Marca>> obtenerTodas() {
        return ResponseEntity.ok(marcaService.obtenerTodas());
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Marca>> obtenerActivas() {
        return ResponseEntity.ok(marcaService.obtenerActivas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.obtenerPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<Marca> crear(@RequestBody Marca marca) {
        return ResponseEntity.ok(marcaService.crear(marca));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(@PathVariable Long id, @RequestBody Marca marca) {
        return ResponseEntity.ok(marcaService.actualizar(id, marca));
    }
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        marcaService.desactivar(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        marcaService.activar(id);
        return ResponseEntity.ok().build();
    }
}