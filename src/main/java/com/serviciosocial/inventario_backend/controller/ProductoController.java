package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.dto.MessageResponse;
import com.serviciosocial.inventario_backend.dto.ProductoBajaRequest;
import com.serviciosocial.inventario_backend.dto.ProductoRequest;
import com.serviciosocial.inventario_backend.model.Producto;
import com.serviciosocial.inventario_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }
    
    @GetMapping("/paginado")
    public ResponseEntity<Page<Producto>> obtenerTodosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.obtenerTodosPaginados(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            Producto producto = productoService.crear(request, usuarioActual);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error al crear producto: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, 
                                       @RequestBody ProductoRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            Producto producto = productoService.actualizar(id, request, usuarioActual);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error al actualizar producto: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Long id, 
                                       @RequestBody ProductoBajaRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            productoService.darDeBaja(id, request, usuarioActual);
            return ResponseEntity.ok(new MessageResponse("Producto dado de baja exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error al dar de baja producto: " + e.getMessage()));
        }
    }
    
    @GetMapping("/filtrar/area/{idArea}")
    public ResponseEntity<List<Producto>> filtrarPorArea(@PathVariable Long idArea) {
        return ResponseEntity.ok(productoService.filtrarPorArea(idArea));
    }
    
    @GetMapping("/filtrar/categoria/{idCategoria}")
    public ResponseEntity<List<Producto>> filtrarPorCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(productoService.filtrarPorCategoria(idCategoria));
    }
    
    @GetMapping("/filtrar/estado/{idEstado}")
    public ResponseEntity<List<Producto>> filtrarPorEstado(@PathVariable Long idEstado) {
        return ResponseEntity.ok(productoService.filtrarPorEstado(idEstado));
    }
}