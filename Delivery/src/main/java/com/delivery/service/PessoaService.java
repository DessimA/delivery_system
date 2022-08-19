package com.delivery.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.delivery.model.Pessoa;
import com.delivery.model.Role;
import com.delivery.repository.PessoaRepository; 

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Pessoa getPessoaSession() {
		Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetails user = (UserDetails) auth;
		
		Pessoa pessoa = buscarProEmail(user.getUsername());
		return pessoa;
	}
	
	public void cadastrar(Pessoa pessoa) {
		
		pessoa.setSenha(new BCryptPasswordEncoder().encode(pessoa.getSenha()));
		
		Role role = new Role();
		role.setPapel("ROLE_USER");
		
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		pessoa.setRoles(roles);
		
		pessoaRepository.save(pessoa);
	}

	

	public Pessoa buscarPorId(Long codigo) {
		return pessoaRepository.getOne(codigo);
	}
	
	public Pessoa buscarProEmail(String email) {
		return pessoaRepository.findByEmail(email);
	}
	
}
