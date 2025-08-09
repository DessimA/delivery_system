package com.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delivery.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{

}
