package com.delivery.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.delivery.security.UserDetailsServiceImplementacao;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserDetailsServiceImplementacao userDetaisServiceImple;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{ 
		http.csrf().disable().authorizeRequests()
		
		.antMatchers("/").permitAll()
		.antMatchers("/pessoa/formulario").permitAll()
		.antMatchers("/pessoa/salvar").permitAll()
		.antMatchers("/produto/formulario").hasRole("ADMIN")
		.antMatchers("/produto/listar").permitAll()
		.antMatchers("/produto/cardapio").permitAll()
		.antMatchers("/produto/comprar").hasRole("USER")
		 
		.anyRequest().authenticated()
		
		.and()
		.formLogin()
		.loginPage("/pessoa/logar")
		.permitAll()
		
		//.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
		
		.and()
		.logout()
		.logoutSuccessUrl("/?logout")
		.permitAll();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetaisServiceImple).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring().antMatchers("/css/**", "/js/**","/img/**", "/images/**");
	}


}

