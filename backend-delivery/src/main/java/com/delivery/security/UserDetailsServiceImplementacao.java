package com.delivery.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.delivery.model.Usuario;
import com.delivery.repository.UsuarioRepository;

@Repository
@Transactional
public class UserDetailsServiceImplementacao implements UserDetailsService{
	
	private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImplementacao.class);

	@Autowired
	private UsuarioRepository usuarioRepository;

	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	            Usuario usuario = usuarioRepository.findByEmail(email);
	            
	            if(usuario == null) {
	                throw new UsernameNotFoundException("Usuário não encontrado");
	            }

                // Log the authorities loaded from the database
                logger.info("Authorities loaded for user {}: {}", email, usuario.getAuthorities());
	        
	        return new User(usuario.getUsername(),usuario.getPassword(),true,true,true,true,usuario.getAuthorities());
	    }
}
