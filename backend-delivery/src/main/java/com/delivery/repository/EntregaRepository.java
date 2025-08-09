package com.delivery.repository;

import com.delivery.model.Entrega;
import com.delivery.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    List<Entrega> findByEntregador(Pessoa entregador);
}
