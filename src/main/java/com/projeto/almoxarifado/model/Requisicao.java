package com.projeto.almoxarifado.model;

import com.projeto.almoxarifado.enums.StatusRequisicao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requisicoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Requisicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Usuario funcionario;

    @OneToMany(mappedBy = "requisicao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemRequisicao> itens;

    @Enumerated(EnumType.STRING)
    private StatusRequisicao status;

    private LocalDateTime dataRequisicao;
    private LocalDateTime dataAprovacao;
    private String observacao;
    private boolean autorizacaoProfessor = false;
}
