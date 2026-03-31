package com.projeto.almoxarifado.controller;

import com.projeto.almoxarifado.dto.RequisicaoRequest;
import com.projeto.almoxarifado.model.Usuario;
import com.projeto.almoxarifado.service.RequisicaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requisicoes")
@RequiredArgsConstructor
public class RequisicaoController {

    private final RequisicaoService requisicaoService;

    @PostMapping
    public ResponseEntity<?> criarRequisicao(
            @RequestBody RequisicaoRequest request,
            Authentication authentication) {

        Usuario aluno = (Usuario) authentication.getPrincipal();

        try {
            return ResponseEntity.ok(requisicaoService.criarRequisicao(request, aluno));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}