package com.projeto.almoxarifado.service;

import com.projeto.almoxarifado.config.JwtUtil;
import com.projeto.almoxarifado.dto.LoginRequest;
import com.projeto.almoxarifado.dto.LoginResponse;
import com.projeto.almoxarifado.dto.UsuarioRequest;
import com.projeto.almoxarifado.model.Usuario;
import com.projeto.almoxarifado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar por username ou email
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public Usuario register(UsuarioRequest request) {
        // Verificar se username já existe
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username já existe!");
        }

        // Verificar se email já existe (se tiver método no repository)
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNome(request.getNome());
        usuario.setTurma(request.getTurma());
        usuario.setTipo(request.getTipo());
        usuario.setEmail(request.getEmail());
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            // Autenticar o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Colocar no contexto de segurança
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Buscar o usuário completo
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

            // Gerar o token JWT
            String token = jwtUtil.generateToken(usuario);

            // Construir resposta
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTipo(usuario.getTipo());
            response.setUsername(usuario.getUsername());
            response.setNome(usuario.getNome());
            response.setEmail(usuario.getEmail());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer login: " + e.getMessage());
        }
    }
}