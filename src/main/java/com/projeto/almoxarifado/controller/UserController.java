package com.projeto.almoxarifado.controller;

import com.projeto.almoxarifado.model.User;
import com.projeto.almoxarifado.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/usuarios)
public class UserController {

    private final UserService service;

    public UserController (UserService service) {
        this.service = service;
    }

    @PostMapping
    public User criar(@RequestBody User user) {
        return service.salvar(user);
    }

    @GetMapping
    public List<User> listar() {
        return service.listar();
    }
}
