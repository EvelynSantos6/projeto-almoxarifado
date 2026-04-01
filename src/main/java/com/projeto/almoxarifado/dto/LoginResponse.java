package com.projeto.almoxarifado.dto;

import com.projeto.almoxarifado.enums.Role;
import lombok.Data;

@Data
public class LoginResponse {
    private String username;
    private String nome;
    private String email;
    private Role tipo;
    private String token;
}
