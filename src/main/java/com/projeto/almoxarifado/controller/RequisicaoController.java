package com.projeto.almoxarifado.controller;

import com.projeto.almoxarifado.dto.RequisicaoRequest;
import com.projeto.almoxarifado.model.Requisicao;
import com.projeto.almoxarifado.model.Usuario;
import com.projeto.almoxarifado.service.RequisicaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requisicoes")
public class RequisicaoController {

    @Autowired
    private RequisicaoService requisicaoService;

    @PostMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<?> criarRequisicao(
            @RequestBody RequisicaoRequest request,
            Authentication authentication) {
        try {
            // Forma mais segura de obter o ID do usuário
            Long alunoId = getUsuarioId(authentication);
            Requisicao requisicao = requisicaoService.criarRequisicao(request, alunoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(requisicao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<?> aprovarRequisicao(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long funcionarioId = getUsuarioId(authentication);
            Requisicao requisicao = requisicaoService.aprovarRequisicao(id, funcionarioId);
            return ResponseEntity.ok(requisicao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<List<Requisicao>> listarPendentes() {
        List<Requisicao> pendentes = requisicaoService.listarPendentes();
        return ResponseEntity.ok(pendentes);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<Requisicao>> listarMinhasRequisicoes(Authentication authentication) {
        Long alunoId = getUsuarioId(authentication);
        List<Requisicao> requisicoes = requisicaoService.listarPorAluno(alunoId);
        return ResponseEntity.ok(requisicoes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUNO', 'FUNCIONARIO')")
    public ResponseEntity<?> buscarRequisicaoPorId(@PathVariable Long id, Authentication authentication) {
        try {
            Requisicao requisicao = requisicaoService.buscarPorId(id);

            // Verificar se o aluno só pode ver suas próprias requisições
            Usuario usuario = (Usuario) authentication.getPrincipal();
            if (usuario.getTipo().name().equals("ALUNO") && !requisicao.getAluno().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para visualizar esta requisição");
            }

            return ResponseEntity.ok(requisicao);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/rejeitar")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<?> rejeitarRequisicao(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo,
            Authentication authentication) {
        try {
            Long funcionarioId = getUsuarioId(authentication);
            Requisicao requisicao = requisicaoService.rejeitarRequisicao(id, funcionarioId, motivo);
            return ResponseEntity.ok(requisicao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<?> cancelarRequisicao(@PathVariable Long id, Authentication authentication) {
        try {
            Long alunoId = getUsuarioId(authentication);
            requisicaoService.cancelarRequisicao(id, alunoId);
            return ResponseEntity.ok("Requisição cancelada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Método auxiliar para extrair o ID do usuário de forma segura
    private Long getUsuarioId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario) {
            return ((Usuario) principal).getId();
        } else if (principal instanceof UserDetails) {
            // Se precisar buscar do banco pelo username
            String username = ((UserDetails) principal).getUsername();
            // Aqui você precisaria injetar o UserService para buscar o usuário
            // return userService.findByUsername(username).getId();
            throw new RuntimeException("Usuário não encontrado");
        }
        throw new RuntimeException("Autenticação inválida");
    }
}
