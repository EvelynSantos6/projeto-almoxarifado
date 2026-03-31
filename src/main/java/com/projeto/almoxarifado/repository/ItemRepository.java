package com.projeto.almoxarifado.repository;

import com.projeto.almoxarifado.enums.TipoItem;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<AbstractReadWriteAccess.Item, Long> {
    List<AbstractReadWriteAccess.Item> findByTipo(TipoItem tipo);
    List<AbstractReadWriteAccess.Item> findByQuantidadeEstoqueLessThan(Integer quantidade);
}
