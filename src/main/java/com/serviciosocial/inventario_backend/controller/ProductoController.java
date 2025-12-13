package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.dto.MessageResponse;
import com.serviciosocial.inventario_backend.dto.ProductoBajaRequest;
import com.serviciosocial.inventario_backend.dto.ProductoRequest;
import com.serviciosocial.inventario_backend.model.Producto;
import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.ProductoRepository;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
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
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
    	return ResponseEntity.ok(productoService.obtenerActivos()); 
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> obtenerActivos() {
        return ResponseEntity.ok(productoService.obtenerActivos());
    }

    @GetMapping("/inactivos")
    public ResponseEntity<?> obtenerInactivos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo admin puede ver inactivos
        if (user.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos para ver productos inactivos"));
        }
        
        return ResponseEntity.ok(productoService.obtenerInactivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<?> obtenerTodosInclusoInactivos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        
        Usuario user = usuarioRepository.findByUsuario(usuarioActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Solo admin puede ver todos
        if (user.getNivel() != 1) {
            return ResponseEntity.status(403)
                .body(new MessageResponse("No tienes permisos"));
        }
        
        return ResponseEntity.ok(productoService.obtenerTodos());
    }
    
    @GetMapping("/paginado")
    public ResponseEntity<Page<Producto>> obtenerTodosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (incluirInactivos) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            Usuario user = usuarioRepository.findByUsuario(usuarioActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            if (user.getNivel() != 1) {
                // Usuario normal solo ve activos
                return ResponseEntity.ok(productoService.obtenerActivosPaginados(pageable));
            }
            
            return ResponseEntity.ok(productoService.obtenerTodosPaginados(pageable));
        }
        
        // Por defecto, solo activos
        return ResponseEntity.ok(productoService.obtenerActivosPaginados(pageable));
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
    
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Long id, 
                                        @RequestBody ProductoBajaRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            productoService.darDeBaja(id, request, usuarioActual);
            return ResponseEntity.ok(new MessageResponse("Producto desactivado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error al desactivar producto: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            // Solo admin puede activar
            Usuario user = usuarioRepository.findByUsuario(usuarioActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            if (user.getNivel() != 1) {
                return ResponseEntity.status(403)
                    .body(new MessageResponse("No tienes permisos para activar productos"));
            }
            
            productoService.reactivar(id, usuarioActual);
            return ResponseEntity.ok(new MessageResponse("Producto activado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error al activar producto: " + e.getMessage()));
        }
    }
    
    public List<Producto> filtrarPorArea(Long idArea) {
        return productoRepository.findAll().stream()
            .filter(Producto::getActivo)  // ← AGREGAR
            .filter(p -> p.getArea().getIdArea().equals(idArea))
            .toList();
    }

    public List<Producto> filtrarPorCategoria(Long idCategoria) {
        return productoRepository.findAll().stream()
            .filter(Producto::getActivo)  // ← AGREGAR
            .filter(p -> p.getCategoria().getIdCategoria().equals(idCategoria))
            .toList();
    }

    public List<Producto> filtrarPorEstado(Long idEstado) {
        return productoRepository.findAll().stream()
            .filter(Producto::getActivo)  // ← AGREGAR
            .filter(p -> p.getEstado().getIdEstado().equals(idEstado))
            .toList();
    }
}