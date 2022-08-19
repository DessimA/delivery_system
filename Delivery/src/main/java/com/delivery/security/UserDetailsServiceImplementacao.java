package com.delivery.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.delivery.model.Pessoa;
import com.delivery.repository.PessoaRepository;

@Repository
@Transactional
public class UserDetailsServiceImplementacao implements UserDetailsService{
	
	@Autowired
	private PessoaRepository pessoaRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
			Pessoa pessoa = pessoaRepository.findByEmail(email);
			
			if(pessoa == null) {
				throw new UsernameNotFoundException("Pessoa não encontrada");
			}
		
		return new User(pessoa.getUsername(),pessoa.getPassword(),true,true,true,true,pessoa.getAuthorities());
	}

}
