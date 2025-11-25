package com.serviciosocial.inventario_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Long idUsuario;
    
    @Column(nullable = false, unique = true, length = 100)
    private String usuario;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 255)
    private String contrasena;
    
    @Column(nullable = false)
    private Integer nivel; // 1 = Admin, 2 = Usuario normal
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(columnDefinition = "TEXT")
    private String permisosAreas; // JSON: [1, 2, 3] - IDs de áreas permitidas
}
