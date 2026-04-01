package com.projeto.almoxarifado.model;

import com.projeto.almoxarifado.enums.TipoItem;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Id;

@Entity
@Table(name = "itens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoItem tipo;

    private Integer quantidadeEstoque;

    private Integer quantidadeDisponivel;

    private boolean ativo = true;
}
