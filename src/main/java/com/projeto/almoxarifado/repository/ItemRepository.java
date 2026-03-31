package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.model.Item;
import com.projeto.almoxarifado.enums.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByTipo(TipoItem tipo);
    List<Item> findByAtivoTrue();
}
