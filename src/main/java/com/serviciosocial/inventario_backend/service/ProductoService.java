package com.serviciosocial.inventario_backend.service;

import com.serviciosocial.inventario_backend.dto.ProductoBajaRequest;
import com.serviciosocial.inventario_backend.dto.ProductoRequest;
import com.serviciosocial.inventario_backend.model.*;
import com.serviciosocial.inventario_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private AreaRepository areaRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private MarcaRepository marcaRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
    
    public Page<Producto> obtenerTodosPaginados(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }
    
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    
    @Transactional
    public Producto crear(ProductoRequest request, String usuarioActual) {
        // Validar número de inventario único (solo si viene)
        if (request.getNoInv() != null && !request.getNoInv().isEmpty()) {
            if (productoRepository.existsByNoInv(request.getNoInv())) {
                throw new RuntimeException("El número de inventario '" + request.getNoInv() + "' ya existe");
            }
        }
        
        // Validar número de serie único (solo si viene)
        if (request.getNoSerie() != null && !request.getNoSerie().isEmpty()) {
            if (productoRepository.existsByNoSerie(request.getNoSerie())) {
                throw new RuntimeException("El número de serie '" + request.getNoSerie() + "' ya está registrado. El producto ya existe en el sistema.");
            }
        }
        
        // Validar que existan las entidades relacionadas
        Area area = areaRepository.findById(request.getIdArea())
            .orElseThrow(() -> new RuntimeException("Área no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Marca marca = marcaRepository.findById(request.getIdMarca())
            .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        Estado estado = estadoRepository.findById(request.getIdEstado())
            .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        
        // Crear producto
        Producto producto = new Producto();
        producto.setArea(area);
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setNoSerie(request.getNoSerie());
        producto.setNoInv(request.getNoInv());
        producto.setEstado(estado);
        producto.setModelo(request.getModelo());
        producto.setFoto(request.getFoto());
        
        producto = productoRepository.save(producto);
        
        // Registrar movimiento
        String detalleInv = producto.getNoInv() != null ? " (Inv: " + producto.getNoInv() + ")" : " (Sin No. Inv)";
        registrarMovimiento(
            usuarioActual,
            "INSERT",
            "Producto creado: " + producto.getModelo() + detalleInv,
            "Productos",
            producto.getIdProducto()
        );
        
        return producto;
    }
    
    @Transactional
    public Producto actualizar(Long id, ProductoRequest request, String usuarioActual) {
        Producto producto = obtenerPorId(id);
        
        String detallesAntes = "Modelo: " + producto.getModelo() + 
                              ", Estado: " + producto.getEstado().getNombre() +
                              ", Área: " + producto.getArea().getNombre();
        
        // Validar número de inventario único (si se está cambiando y viene)
        if (request.getNoInv() != null && !request.getNoInv().isEmpty() && 
            !request.getNoInv().equals(producto.getNoInv())) {
            if (productoRepository.existsByNoInv(request.getNoInv())) {
                throw new RuntimeException("El número de inventario '" + request.getNoInv() + "' ya existe");
            }
        }
        
        // Validar número de serie único (si se está cambiando y viene)
        if (request.getNoSerie() != null && !request.getNoSerie().isEmpty() && 
            !request.getNoSerie().equals(producto.getNoSerie())) {
            if (productoRepository.existsByNoSerie(request.getNoSerie())) {
                throw new RuntimeException("El número de serie '" + request.getNoSerie() + "' ya está registrado");
            }
        }
        
        // Actualizar relaciones
        if (request.getIdArea() != null) {
            Area area = areaRepository.findById(request.getIdArea())
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));
            producto.setArea(area);
        }
        
        if (request.getIdCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }
        
        if (request.getIdMarca() != null) {
            Marca marca = marcaRepository.findById(request.getIdMarca())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
            producto.setMarca(marca);
        }
        
        if (request.getIdEstado() != null) {
            Estado estado = estadoRepository.findById(request.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            producto.setEstado(estado);
        }
        
        // Actualizar campos simples
        if (request.getNoSerie() != null) {
            producto.setNoSerie(request.getNoSerie());
        }
        if (request.getNoInv() != null) {
            producto.setNoInv(request.getNoInv());
        }
        if (request.getModelo() != null) {
            producto.setModelo(request.getModelo());
        }
        if (request.getFoto() != null) {
            producto.setFoto(request.getFoto());
        }
        
        producto = productoRepository.save(producto);
        
        // Registrar movimiento
        String detallesDespues = "Modelo: " + producto.getModelo() + 
                                ", Estado: " + producto.getEstado().getNombre() +
                                ", Área: " + producto.getArea().getNombre();
        
        registrarMovimiento(
            usuarioActual,
            "UPDATE",
            "Producto actualizado. Antes: [" + detallesAntes + "] - Después: [" + detallesDespues + "]",
            "Productos",
            producto.getIdProducto()
        );
        
        return producto;
    }
    
    @Transactional
    public void darDeBaja(Long id, ProductoBajaRequest request, String usuarioActual) {
        Producto producto = obtenerPorId(id);
        
        // Simplemente desactivar el producto (borrado lógico)
        producto.setActivo(false);
        productoRepository.save(producto);
        
        // Registrar movimiento como BAJA con el motivo
        registrarMovimiento(
            usuarioActual,
            "BAJA",
            "Producto dado de baja. Motivo: " + request.getMotivo() + 
            " - Producto: " + producto.getModelo() + 
            (producto.getNoInv() != null ? " (Inv: " + producto.getNoInv() + ")" : ""),
            "Productos",
            producto.getIdProducto()
        );
    }
    
    @Transactional
    public void reactivar(Long id, String usuarioActual) {
        Producto producto = obtenerPorId(id);
        
        if (producto.getActivo()) {
            throw new RuntimeException("El producto ya está activo");
        }
        
        producto.setActivo(true);
        productoRepository.save(producto);
        
        registrarMovimiento(
            usuarioActual,
            "REACTIVACION",
            "Producto reactivado: " + producto.getModelo() + 
            (producto.getNoInv() != null ? " (Inv: " + producto.getNoInv() + ")" : ""),
            "Productos",
            producto.getIdProducto()
        );
    }
    
    // Método auxiliar para registrar movimientos
    private void registrarMovimiento(String nombreUsuario, String accion, 
                                     String detalles, String tablaAfectada, 
                                     Long idRegistroAfectado) {
        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Movimiento movimiento = new Movimiento();
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDate.now());
        movimiento.setHora(LocalTime.now());
        movimiento.setAccion(accion);
        movimiento.setDetalles(detalles);
        movimiento.setTablaAfectada(tablaAfectada);
        movimiento.setIdRegistroAfectado(idRegistroAfectado);
        
        movimientoRepository.save(movimiento);
    }
    
    // Métodos de filtrado
    public List<Producto> filtrarPorArea(Long idArea) {
        return productoRepository.findAll().stream()
            .filter(p -> p.getArea().getIdArea().equals(idArea))
            .toList();
    }
    
    public List<Producto> filtrarPorCategoria(Long idCategoria) {
        return productoRepository.findAll().stream()
            .filter(p -> p.getCategoria().getIdCategoria().equals(idCategoria))
            .toList();
    }
    
    public List<Producto> filtrarPorEstado(Long idEstado) {
        return productoRepository.findAll().stream()
            .filter(p -> p.getEstado().getIdEstado().equals(idEstado))
            .toList();
    }
    
    public List<Producto> obtenerActivos() {
        return productoRepository.findAll().stream()
            .filter(Producto::getActivo)
            .toList();
    }

    public List<Producto> obtenerInactivos() {
        return productoRepository.findAll().stream()
            .filter(p -> !p.getActivo())
            .toList();
    }

    public Page<Producto> obtenerActivosPaginados(Pageable pageable) {
        List<Producto> activos = productoRepository.findAll().stream()
            .filter(Producto::getActivo)
            .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), activos.size());
        
        return new PageImpl<>(
            activos.subList(start, end),
            pageable,
            activos.size()
        );
    }
}