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
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public Usuario register(UsuarioRequest request) {
        // Verificar se usuário já existe
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Usuário já existe!");
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTipo(usuario.getTipo());
            response.setUsername(usuario.getUsername());
            response.setNome(usuario.getNome());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer login: " + e.getMessage());
        }
    }
}
