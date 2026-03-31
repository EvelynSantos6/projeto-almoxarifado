package com.projeto.almoxarifado.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "itens_requisicao")
@Data
public class ItemRequisicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requisicao_id")
    private Requisicao requisicao;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private AbstractReadWriteAccess.Item item;

    private Integer quantidade;

    private Integer quantidadeDevolvida;

    private Boolean devolvido = false;
}
