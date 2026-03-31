package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.model.Devolucao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevolucaoRepository extends JpaRepository<Devolucao, Long> {
    List<Devolucao> findByRequisicaoId(Long requisicaoId);
    List<Devolucao> findByItemId(Long itemId);
}
