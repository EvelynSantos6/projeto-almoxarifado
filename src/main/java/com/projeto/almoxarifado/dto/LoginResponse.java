package com.projeto.almoxarifado.dto;

import com.projeto.almoxarifado.enums.TipoUsuario;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private TipoUsuario tipo;
    private String username;
    private String nome;
}
