package com.projeto.almoxarifado.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RequisicaoRequest {
    private Map<Long, Integer> itens;  // Chave: ID do item, Valor: quantidade
    private boolean autorizacaoProfessor;
}
