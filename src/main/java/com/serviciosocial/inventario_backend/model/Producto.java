package com.serviciosocial.inventario_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto")
    private Long idProducto;
    
    @ManyToOne
    @JoinColumn(name = "idarea", nullable = false)
    private Area area;
    
    @ManyToOne
    @JoinColumn(name = "idcategoria", nullable = false)
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "idmarca", nullable = false)
    private Marca marca;
    
    @Column(name = "noserie", unique = true)
    private Long noSerie;
    
    @Column(name = "noinv", nullable = false, unique = true)
    private Long noInv;
    
    @ManyToOne
    @JoinColumn(name = "idestado", nullable = false)
    private Estado estado;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column
    private String foto;
}