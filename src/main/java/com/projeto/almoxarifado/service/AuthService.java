package com.projeto.almoxarifado.service;

import com.projeto.almoxarifado.dto.LoginRequest;
import com.projeto.almoxarifado.dto.LoginResponse;
import com.projeto.almoxarifado.dto.UsuarioRequest;
import com.projeto.almoxarifado.model.Usuario;
import com.projeto.almoxarifado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public Usuario register(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nome de usuário já existe!");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTurma(request.getTurma());
        usuario.setTipo(request.getTipo());
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        LoginResponse response = new LoginResponse();
        response.setUsername(usuario.getUsername());
        response.setNome(usuario.getNome());
        response.setTipo(usuario.getTipo());
        response.setEmail(usuario.getEmail());

        return response;
    }
}