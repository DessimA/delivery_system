package com.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delivery.model.Usuario;

public  interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	//Usuario findByLogin(String login);

	Usuario findByEmail(String email);
}