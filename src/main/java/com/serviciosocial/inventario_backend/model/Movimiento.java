package com.serviciosocial.inventario_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmovimiento")
    private Long idMovimiento;
    
    @ManyToOne
    @JoinColumn(name = "idusuario", nullable = false)
    @JsonIgnoreProperties({"contrasena"}) 
    private Usuario usuario;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(nullable = false)
    private LocalTime hora;
    
    @Column(nullable = false)
    private String accion; // INSERT, UPDATE, DELETE
    
    @Column(columnDefinition = "TEXT")
    private String detalles;
    
    @Column(name = "tablaafectada", nullable = false)
    private String tablaAfectada;
    
    @Column(name = "idregistroafectado", nullable = false)
    private Long idRegistroAfectado;
}