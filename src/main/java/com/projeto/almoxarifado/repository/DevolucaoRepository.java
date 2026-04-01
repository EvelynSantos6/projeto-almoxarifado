package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.model.Devolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DevolucaoRepository extends JpaRepository<Devolucao, Long> {
    // O Spring agora encontra "id" dentro de "Requisicao" automaticamente
    List<Devolucao> findByRequisicaoId(Long requisicaoId);

    List<Devolucao> findByItemId(Long itemId);
}
