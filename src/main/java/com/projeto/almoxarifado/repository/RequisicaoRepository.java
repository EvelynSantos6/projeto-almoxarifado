package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.model.Requisicao;
import com.projeto.almoxarifado.enums.StatusRequisicao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequisicaoRepository extends JpaRepository<Requisicao, Long> {
    List<Requisicao> findByStatus(StatusRequisicao status);
    List<Requisicao> findByAlunoId(Long alunoId);
}
