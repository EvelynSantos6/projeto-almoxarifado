package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.enums.TipoUsuario;
import com.projeto.almoxarifado.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByTipo(TipoUsuario tipo);
    Boolean existsByUsername(String username);
}
