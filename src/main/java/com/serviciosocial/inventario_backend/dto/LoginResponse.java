package com.serviciosocial.inventario_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private Long idUsuario;
    private String usuario;
    private String nombre;
    private Integer nivel;
    
    public LoginResponse(String token, Long idUsuario, String usuario, String nombre, Integer nivel) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.nombre = nombre;
        this.nivel = nivel;
    }
}