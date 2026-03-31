package com.projeto.almoxarifado.dto;

import com.projeto.almoxarifado.enums.TipoUsuario;
import lombok.Data;

@Data
public class UsuarioRequest {
    private String username;
    private String password;
    private String nome;
    private String turma;
    private String tipo;  // "ALUNO" ou "FUNCIONARIO"
    private String email;
}
