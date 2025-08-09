package com.delivery.repository;

import com.delivery.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    List<Produto> findByEstabelecimentoId(Long estabelecimentoId);
}
