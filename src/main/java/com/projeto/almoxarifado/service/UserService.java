package com.projeto.almoxarifado.service;

import com.projeto.almoxarifado.model.User;
import com.projeto.almoxarifado.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    //Criar usuário
    public User criar(User user) {

        Optional<User> existente = repository.findByEmail(user.getEmail());

        if(existente.isPresent()) {
            throw new RuntimeException("Email já cadadstrado!");
        }
        return repository.save(user);
    }

    //Login
    public User login (String email, String senha) {

        Optional<User> usuarioOptional = repository.findByEmail(email);

        if (!usuarioOptional.isPresent()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        User user = usuarioOptional.get();

        if (!user.getSenha().equals(senha)) {
            throw new RuntimeException("Senha inválida");
        }

        return user;
    }

    public List<User> listar () {
        return repository.findAll();
    }
}
