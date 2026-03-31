package com.projeto.almoxarifado.service;

import com.projeto.almoxarifado.dto.RequisicaoRequest;
import com.projeto.almoxarifado.model.Item;
import com.projeto.almoxarifado.model.ItemRequisicao;
import com.projeto.almoxarifado.model.Requisicao;
import com.projeto.almoxarifado.model.Usuario;
import com.projeto.almoxarifado.enums.StatusRequisicao;
import com.projeto.almoxarifado.enums.TipoItem;
import com.projeto.almoxarifado.repository.ItemRepository;
import com.projeto.almoxarifado.repository.RequisicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class RequisicaoService {

    private final RequisicaoRepository requisicaoRepository;
    private final ItemRepository itemRepository;

    public Requisicao criarRequisicao(RequisicaoRequest request, Usuario aluno) {
        // 1. Gerar número aleatório da requisição
        Long numeroRequisicao = gerarNumeroRequisicao();

        // 2. Criar a requisição
        Requisicao requisicao = new Requisicao();
        requisicao.setNumeroRequisicao(numeroRequisicao);
        requisicao.setAluno(aluno);
        requisicao.setStatus(StatusRequisicao.PENDENTE);
        requisicao.setDataRequisicao(LocalDateTime.now());
        requisicao.setAutorizacaoProfessor(request.isAutorizacaoProfessor());
        requisicao.setItens(new ArrayList<>());

        // 3. Processar cada item da requisição (usando Map)
        for (Map.Entry<Long, Integer> entry : request.getItens().entrySet()) {
            Long itemId = entry.getKey();
            Integer quantidade = entry.getValue();

            // Buscar o item no banco de dados
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item não encontrado com ID: " + itemId));

            // Validar se tem estoque disponível
            if (item.getQuantidadeDisponivel() < quantidade) {
                throw new RuntimeException(
                        "Estoque insuficiente para o item: " + item.getNome() +
                                ". Disponível: " + item.getQuantidadeDisponivel() +
                                ", Solicitado: " + quantidade
                );
            }

            // Validar se é item restrito e tem autorização
            if (item.getTipo() == TipoItem.RESTRITO && !request.isAutorizacaoProfessor()) {
                throw new RuntimeException(
                        "ATENÇÃO: " + item.getNome() + " é um item restrito! " +
                                "Você precisa apresentar autorização do professor para retirar este item."
                );
            }

            // Criar o item da requisição
            ItemRequisicao itemRequisicao = new ItemRequisicao();
            itemRequisicao.setItem(item);
            itemRequisicao.setQuantidade(quantidade);
            itemRequisicao.setRequisicao(requisicao);

            requisicao.getItens().add(itemRequisicao);
        }

        // 4. Salvar a requisição
        return requisicaoRepository.save(requisicao);
    }

    public Requisicao aprovarRequisicao(Long numeroRequisicao, Usuario funcionario) {
        // 1. Buscar a requisição
        Requisicao requisicao = requisicaoRepository.findById(numeroRequisicao)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada: " + numeroRequisicao));

        // 2. Validar se está pendente
        if (requisicao.getStatus() != StatusRequisicao.PENDENTE) {
            throw new RuntimeException(
                    "Esta requisição já foi processada. Status atual: " + requisicao.getStatus()
            );
        }

        // 3. Processar cada item
        for (ItemRequisicao itemReq : requisicao.getItens()) {
            Item item = itemReq.getItem();

            // Validar item restrito
            if (item.getTipo() == TipoItem.RESTRITO && !requisicao.isAutorizacaoProfessor()) {
                throw new RuntimeException(
                        "Item restrito '" + item.getNome() +
                                "' requer autorização do professor para ser retirado"
                );
            }

            // Subtrair do estoque
            int novaQuantidade = item.getQuantidadeDisponivel() - itemReq.getQuantidade();
            if (novaQuantidade < 0) {
                throw new RuntimeException(
                        "Erro no estoque do item: " + item.getNome()
                );
            }

            item.setQuantidadeDisponivel(novaQuantidade);
            itemRepository.save(item);
        }

        // 4. Atualizar a requisição
        requisicao.setStatus(StatusRequisicao.APROVADA);
        requisicao.setFuncionario(funcionario);
        requisicao.setDataAprovacao(LocalDateTime.now());

        return requisicaoRepository.save(requisicao);
    }

    /**
     * Gerar número aleatório de 3 dígitos para a requisição
     */
    private Long gerarNumeroRequisicao() {
        Long numero;
        do {
            numero = ThreadLocalRandom.current().nextLong(100, 1000);
        } while (requisicaoRepository.existsById(numero));
        return numero;
    }
}