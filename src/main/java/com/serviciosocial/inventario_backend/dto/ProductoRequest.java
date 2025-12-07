package com.serviciosocial.inventario_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {
    private Long idArea;
    private Long idCategoria;
    private Long idMarca;
    private String noSerie;
    private String noInv;
    private Long idEstado;
    private String modelo;
    private String foto;
}