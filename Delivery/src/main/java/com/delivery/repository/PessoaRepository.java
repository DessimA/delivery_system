package com.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delivery.model.Pessoa;

public  interface PessoaRepository extends JpaRepository<Pessoa, Long>{
	//Pessoa findByLogin(String login);

	Pessoa findByEmail(String email);
}