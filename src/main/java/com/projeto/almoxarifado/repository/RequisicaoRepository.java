package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.enums.StatusRequisicao;
import com.projeto.almoxarifado.model.Requisicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequisicaoRepository extends JpaRepository<Requisicao, Long> {
    List<Requisicao> findByAlunoId(Long alunoId);
    List<Requisicao> findByStatus(StatusRequisicao status);
    List<Requisicao> findByStatusAndPrecisaAutorizacao(StatusRequisicao status, Boolean precisaAutorizacao);
    List<Requisicao> findByFuncionarioAprovadorId(Long funcionarioId);
}
