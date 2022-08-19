package com.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delivery.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long>{

}
