package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
}
