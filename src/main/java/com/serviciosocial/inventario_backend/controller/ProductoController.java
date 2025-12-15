package com.serviciosocial.inventario_backend.controller;

import com.serviciosocial.inventario_backend.dto.MessageResponse;
import com.serviciosocial.inventario_backend.dto.ProductoBajaRequest;
import com.serviciosocial.inventario_backend.dto.ProductoRequest;
import com.serviciosocial.inventario_backend.model.Producto;
import com.serviciosocial.inventario_backend.model.Usuario;
import com.serviciosocial.inventario_backend.repository.ProductoRepository;
import com.serviciosocial.inventario_backend.repository.UsuarioRepository;
import com.serviciosocial.inventario_backend.service.CloudinaryService;
import com.serviciosocial.inventario_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
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
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
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
    public ResponseEntity<?> crear(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("idArea") Long idArea,
            @RequestParam("idCategoria") Long idCategoria,
            @RequestParam("idMarca") Long idMarca,
            @RequestParam(value = "noSerie", required = false) String noSerie,
            @RequestParam(value = "noInv", required = false) String noInv,
            @RequestParam("idEstado") Long idEstado,
            @RequestParam("modelo") String modelo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            // Subir imagen a Cloudinary si viene
            String fotoUrl = null;
            if (file != null && !file.isEmpty()) {
                fotoUrl = cloudinaryService.subirImagen(file);
            }
            
            // Convertir cadenas vacías a null
            String noSerieNormalizado = (noSerie != null && noSerie.trim().isEmpty()) ? null : noSerie;
            String noInvNormalizado = (noInv != null && noInv.trim().isEmpty()) ? null : noInv;
            
            // Crear request
            ProductoRequest request = new ProductoRequest();
            request.setIdArea(idArea);
            request.setIdCategoria(idCategoria);
            request.setIdMarca(idMarca);
            request.setNoSerie(noSerieNormalizado);
            request.setNoInv(noInvNormalizado);
            request.setIdEstado(idEstado);
            request.setModelo(modelo);
            request.setFoto(fotoUrl);
            
            Producto producto = productoService.crear(request, usuarioActual);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error al crear producto: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "idArea", required = false) Long idArea,
            @RequestParam(value = "idCategoria", required = false) Long idCategoria,
            @RequestParam(value = "idMarca", required = false) Long idMarca,
            @RequestParam(value = "noSerie", required = false) String noSerie,
            @RequestParam(value = "noInv", required = false) String noInv,
            @RequestParam(value = "idEstado", required = false) Long idEstado,
            @RequestParam(value = "modelo", required = false) String modelo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            
            // Obtener producto actual para acceder a la foto vieja
            Producto productoExistente = productoService.obtenerPorId(id);
            String fotoViejaUrl = productoExistente.getFoto();
            
            // Subir nueva imagen a Cloudinary si viene
            String fotoUrl = null;
            if (file != null && !file.isEmpty()) {
                fotoUrl = cloudinaryService.subirImagen(file);
                
                // Eliminar la imagen anterior de Cloudinary
                if (fotoViejaUrl != null && fotoViejaUrl.contains("cloudinary.com")) {
                    try {
                        String publicId = cloudinaryService.extraerPublicId(fotoViejaUrl);
                        if (publicId != null) {
                            cloudinaryService.eliminarImagen(publicId);
                        }
                    } catch (IOException e) {
                        // Log error pero no fallar la operación
                        System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                    }
                }
            }
            
            // Convertir cadenas vacías a null
            String noSerieNormalizado = (noSerie != null && noSerie.trim().isEmpty()) ? null : noSerie;
            String noInvNormalizado = (noInv != null && noInv.trim().isEmpty()) ? null : noInv;
            
            // Crear request
            ProductoRequest request = new ProductoRequest();
            request.setIdArea(idArea);
            request.setIdCategoria(idCategoria);
            request.setIdMarca(idMarca);
            request.setNoSerie(noSerieNormalizado);
            request.setNoInv(noInvNormalizado);
            request.setIdEstado(idEstado);
            request.setModelo(modelo);
            request.setFoto(fotoUrl);
            
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

    @PostMapping("/upload-imagen")
    public ResponseEntity<?> uploadImagen(@RequestParam("file") MultipartFile file) {
        try {
            // Validar que sea una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("El archivo debe ser una imagen"));
            }
            
            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("La imagen no debe superar 5MB"));
            }
            
            // Subir a Cloudinary
            String imageUrl = cloudinaryService.subirImagen(file);
            
            // Retornar la URL
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.status(500)
                .body(new MessageResponse("Error al subir imagen: " + e.getMessage()));
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