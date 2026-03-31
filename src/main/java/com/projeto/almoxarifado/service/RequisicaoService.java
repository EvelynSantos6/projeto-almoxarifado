package com.projeto.almoxarifado.service;

import com.projeto.almoxarifado.dto.RequisicaoRequest;
import com.projeto.almoxarifado.enums.StatusDevolucao;
import com.projeto.almoxarifado.enums.StatusRequisicao;
import com.projeto.almoxarifado.enums.TipoItem;
import com.projeto.almoxarifado.model.Devolucao;
import com.projeto.almoxarifado.model.Item;
import com.projeto.almoxarifado.model.ItemRequisicao;
import com.projeto.almoxarifado.model.Requisicao;
import com.projeto.almoxarifado.model.Usuario;
import com.projeto.almoxarifado.repository.DevolucaoRepository;
import com.projeto.almoxarifado.repository.ItemRepository;
import com.projeto.almoxarifado.repository.RequisicaoRepository;
import com.projeto.almoxarifado.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class RequisicaoService {

    @Autowired
    private RequisicaoRepository requisicaoRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DevolucaoRepository devolucaoRepository; // ADICIONADO!

    @Transactional
    public Requisicao criarRequisicao(RequisicaoRequest request, Long alunoId) {
        Requisicao requisicao = new Requisicao();
        requisicao.setAluno(usuarioRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado")));
        requisicao.setNumeroRequisicao(gerarNumeroRequisicao());
        requisicao.setStatus(StatusRequisicao.PENDENTE);
        requisicao.setDataRequisicao(LocalDateTime.now());

        boolean precisaAutorizacao = false;

        for (RequisicaoRequest.ItemRequisicaoDTO itemReq : request.getItens()) {
            // CORRIGIDO: usar a classe Item correta, não AbstractReadWriteAccess.Item
            Item item = itemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemReq.getItemId()));

            if (item.getTipo() == TipoItem.RESTRITO) {
                precisaAutorizacao = true;
            }

            if (item.getQuantidadeEstoque() < itemReq.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o item: " + item.getNome() +
                        ". Disponível: " + item.getQuantidadeEstoque());
            }

            ItemRequisicao itemRequisicao = new ItemRequisicao();
            itemRequisicao.setItem(item);
            itemRequisicao.setQuantidade(itemReq.getQuantidade());
            itemRequisicao.setQuantidadeDevolvida(0);
            itemRequisicao.setDevolvido(false);
            itemRequisicao.setRequisicao(requisicao);

            requisicao.getItens().add(itemRequisicao);
        }

        requisicao.setPrecisaAutorizacao(precisaAutorizacao);

        return requisicaoRepository.save(requisicao);
    }

    @Transactional
    public Requisicao aprovarRequisicao(Long requisicaoId, Long funcionarioId) {
        Requisicao requisicao = requisicaoRepository.findById(requisicaoId)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada"));

        Usuario funcionario = usuarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        requisicao.setFuncionarioAprovador(funcionario);
        requisicao.setStatus(StatusRequisicao.APROVADA);
        requisicao.setDataAprovacao(LocalDateTime.now());

        // Subtrair do estoque
        for (ItemRequisicao itemReq : requisicao.getItens()) {
            Item item = itemReq.getItem();
            item.setQuantidadeEstoque(item.getQuantidadeEstoque() - itemReq.getQuantidade());
            itemRepository.save(item);
        }

        return requisicaoRepository.save(requisicao);
    }

    @Transactional
    public Devolucao registrarDevolucao(Long requisicaoId, Long itemId, Integer quantidade, Long funcionarioId) {
        Requisicao requisicao = requisicaoRepository.findById(requisicaoId)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if (item.getTipo() != TipoItem.FERRAMENTA) {
            throw new RuntimeException("Este item não requer devolução");
        }

        // Atualizar quantidade devolvida
        for (ItemRequisicao itemReq : requisicao.getItens()) {
            if (itemReq.getItem().getId().equals(itemId)) {
                int novaQuantidadeDevolvida = itemReq.getQuantidadeDevolvida() + quantidade;

                if (novaQuantidadeDevolvida > itemReq.getQuantidade()) {
                    throw new RuntimeException("Quantidade devolvida excede a quantidade requisitada");
                }

                itemReq.setQuantidadeDevolvida(novaQuantidadeDevolvida);
                if (itemReq.getQuantidadeDevolvida().equals(itemReq.getQuantidade())) {
                    itemReq.setDevolvido(true);
                }
                break;
            }
        }

        // Registrar devolução
        Devolucao devolucao = new Devolucao();
        devolucao.setRequisicao(requisicao);
        devolucao.setItem(item);
        devolucao.setQuantidade(quantidade);
        devolucao.setDataDevolucao(LocalDateTime.now());
        devolucao.setStatus(StatusDevolucao.DEVOLVIDO);

        // Devolver ao estoque
        item.setQuantidadeEstoque(item.getQuantidadeEstoque() + quantidade);
        itemRepository.save(item);

        return devolucaoRepository.save(devolucao);
    }

    // MÉTODOS ADICIONADOS (que faltavam)

    public List<Requisicao> listarPendentes() {
        return requisicaoRepository.findByStatus(StatusRequisicao.PENDENTE);
    }

    public List<Requisicao> listarPorAluno(Long alunoId) {
        return requisicaoRepository.findByAlunoId(alunoId);
    }

    public List<Requisicao> listarTodas() {
        return requisicaoRepository.findAll();
    }

    public Requisicao buscarPorId(Long id) {
        return requisicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada com ID: " + id));
    }

    @Transactional
    public Requisicao rejeitarRequisicao(Long requisicaoId, Long funcionarioId, String motivo) {
        Requisicao requisicao = requisicaoRepository.findById(requisicaoId)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada"));

        Usuario funcionario = usuarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        requisicao.setStatus(StatusRequisicao.REJEITADA);
        requisicao.setFuncionarioAprovador(funcionario);
        requisicao.setObservacao(motivo);

        return requisicaoRepository.save(requisicao);
    }

    @Transactional
    public void cancelarRequisicao(Long requisicaoId, Long alunoId) {
        Requisicao requisicao = requisicaoRepository.findById(requisicaoId)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada"));

        // Verificar se o aluno é o dono da requisição
        if (!requisicao.getAluno().getId().equals(alunoId)) {
            throw new RuntimeException("Você só pode cancelar suas próprias requisições");
        }

        // Verificar se a requisição ainda pode ser cancelada
        if (requisicao.getStatus() != StatusRequisicao.PENDENTE) {
            throw new RuntimeException("Apenas requisições pendentes podem ser canceladas");
        }

        requisicao.setStatus(StatusRequisicao.CANCELADA);
        requisicaoRepository.save(requisicao);
    }

    public List<Requisicao> listarRequisicoesPendentesAutorizacao() {
        return requisicaoRepository.findByStatusAndPrecisaAutorizacao(StatusRequisicao.PENDENTE, true);
    }

    @Transactional
    public Requisicao adicionarAutorizacaoProfessor(Long requisicaoId, String autorizacao) {
        Requisicao requisicao = requisicaoRepository.findById(requisicaoId)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada"));

        requisicao.setAutorizacaoProfessor(autorizacao);

        return requisicaoRepository.save(requisicao);
    }

    private String gerarNumeroRequisicao() {
        Random random = new Random();
        return String.valueOf(100 + random.nextInt(900));
    }
}