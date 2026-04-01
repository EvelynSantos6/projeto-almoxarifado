package com.projeto.almoxarifado.model;

import com.projeto.almoxarifado.enums.TipoItem;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;

@Entity
@Table(name = "itens")
@Data // Esta anotação gera o getTipo() e getQuantidadeDisponivel() automaticamente
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private boolean ativo = true;

    @Enumerated(EnumType.STRING)
    private TipoItem tipo;

    private Integer quantidadeDisponivel;
}
