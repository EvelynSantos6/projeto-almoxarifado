package com.projeto.almoxarifado.dto;

import com.projeto.almoxarifado.enums.Role;
import lombok.Data;

@Data
public class UsuarioRequest {
    private String username;
    private String password;
    private String nome;
    private String email;
    private String turma; // Adicione esta linha
    private Role tipo;    // Certifique-se que o tipo é Role ou TipoUsuario
}
