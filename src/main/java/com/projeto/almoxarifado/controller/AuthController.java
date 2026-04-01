package com.projeto.almoxarifado.controller;

import com.projeto.almoxarifado.config.JwtUtil;
import com.projeto.almoxarifado.dto.LoginRequest;
import com.projeto.almoxarifado.dto.LoginResponse;
import com.projeto.almoxarifado.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        // Gerar o token JWT
        UserDetails userDetails = authService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        loginResponse.setToken(token);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@RequestBody com.projeto.almoxarifado.dto.UsuarioRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}