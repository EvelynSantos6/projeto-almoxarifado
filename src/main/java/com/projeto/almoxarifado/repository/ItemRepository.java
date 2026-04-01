package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
