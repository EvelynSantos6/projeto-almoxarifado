package com.projeto.almoxarifado.model;

import com.projeto.almoxarifado.enums.TipoItem;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;

@Entity
@Table(name = "itens")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // ADICIONE ESTA LINHA SE ELA NÃO EXISTIR
    private boolean ativo = true;

    @Enumerated(EnumType.STRING)
    private TipoItem tipo;
}
