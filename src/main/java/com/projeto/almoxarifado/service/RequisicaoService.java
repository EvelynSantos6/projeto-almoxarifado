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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class RequisicaoService {

    private final RequisicaoRepository requisicaoRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Requisicao criarRequisicao(RequisicaoRequest request, Usuario aluno) {
        // Em vez de gerar fixo, use o seu método privado que checa o banco
        Long numeroRequisicao = gerarNumeroRequisicao();

        Requisicao requisicao = new Requisicao();
        requisicao.setId(numeroRequisicao); // Usando o número aleatório como ID
        requisicao.setAluno(aluno);
        requisicao.setStatus(StatusRequisicao.PENDENTE);
        requisicao.setDataRequisicao(LocalDateTime.now());
        requisicao.setAutorizacaoProfessor(request.isAutorizacaoProfessor());

        List<ItemRequisicao> itensParaSalvar = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : request.getItens().entrySet()) {
            Item item = itemRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Item não encontrado"));

            // Regra de Item Restrito
            if (item.getTipo() == TipoItem.RESTRITO && !request.isAutorizacaoProfessor()) {
                throw new RuntimeException("O item " + item.getNome() + " exige autorização do professor!");
            }

            // Validação de Estoque
            if (item.getQuantidadeDisponivel() < entry.getValue()) {
                throw new RuntimeException("Estoque insuficiente para: " + item.getNome());
            }

            ItemRequisicao ir = new ItemRequisicao();
            ir.setItem(item);
            ir.setQuantidade(entry.getValue());
            ir.setRequisicao(requisicao);
            itensParaSalvar.add(ir);
        }

        requisicao.setItens(itensParaSalvar);
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