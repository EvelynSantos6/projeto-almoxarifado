package com.projeto.almoxarifado.model;

import com.projeto.almoxarifado.enums.StatusRequisicao;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requisicoes")
@Data
public class Requisicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroRequisicao; // Número aleatório tipo "387"

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Usuario funcionarioAprovador;

    @OneToMany(mappedBy = "requisicao", cascade = CascadeType.ALL)
    private List<ItemRequisicao> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusRequisicao status;

    private LocalDateTime dataRequisicao;

    private LocalDateTime dataAprovacao;

    private String observacao;

    private Boolean precisaAutorizacao = false;

    private String autorizacaoProfessor; // caminho do arquivo ou texto
}
