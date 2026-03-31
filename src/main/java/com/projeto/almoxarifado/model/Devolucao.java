package com.projeto.almoxarifado.model;

import com.projeto.almoxarifado.enums.StatusDevolucao;
import jakarta.persistence.*;
import lombok.Data;



import java.time.LocalDateTime;

@Entity
@Table(name = "devolucoes")
@Data
public class Devolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requisicao_id")
    private Requisicao requisicao;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer quantidade;

    private LocalDateTime dataDevolucao;

    @Enumerated(EnumType.STRING)
    private StatusDevolucao status;

    private String observacao;
}
