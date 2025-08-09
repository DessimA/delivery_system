package com.delivery;

import com.delivery.model.Pessoa;
import com.delivery.model.Produto;
import com.delivery.model.Role;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class DeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(PessoaRepository pessoaRepository, RoleRepository roleRepository, ProdutoRepository produtoRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Criar roles se não existirem
			Role adminRole = roleRepository.findByPapel("ADMIN");
			if (adminRole == null) {
				adminRole = new Role();
				adminRole.setPapel("ADMIN");
				roleRepository.save(adminRole);
			}

			Role userRole = roleRepository.findByPapel("USER");
			if (userRole == null) {
				userRole = new Role();
				userRole.setPapel("USER");
				roleRepository.save(userRole);
			}

			// Criar usuário admin padrão se não existir
			if (pessoaRepository.findByEmail("admin@example.com") == null) {
				Pessoa admin = new Pessoa();
				admin.setNome("Admin");
				admin.setEmail("admin@example.com");
				admin.setSenha(passwordEncoder.encode("admin123"));
				admin.setRoles(Collections.singletonList(adminRole));
				pessoaRepository.save(admin);
			}

			// Criar usuário comum padrão se não existir
			if (pessoaRepository.findByEmail("user@example.com") == null) {
				Pessoa user = new Pessoa();
				user.setNome("User");
				user.setEmail("user@example.com");
				user.setSenha(passwordEncoder.encode("user123"));
				user.setRoles(Collections.singletonList(userRole));
				pessoaRepository.save(user);
			}

			// Criar produtos fictícios se não existirem
			if (produtoRepository.count() == 0) {
				Produto p1 = new Produto();
				p1.setNomeProduto("Pizza Calabresa");
				p1.setDescricao("Pizza com molho de tomate, calabresa e queijo.");
				p1.setPreco(45.00f);
				p1.setCaminhoImagem("pizza_calabresa.png");
				produtoRepository.save(p1);

				Produto p2 = new Produto();
				p2.setNomeProduto("Hamburguer X-Tudo");
				p2.setDescricao("Pão, carne, queijo, bacon, ovo, alface e tomate.");
				p2.setPreco(30.00f);
				p2.setCaminhoImagem("hamburguer_x_tudo.png");
				produtoRepository.save(p2);

				Produto p3 = new Produto();
				p3.setNomeProduto("Batata Frita");
				p3.setDescricao("Porção de batatas fritas crocantes.");
				p3.setPreco(15.00f);
				p3.setCaminhoImagem("batata_frita.png");
				produtoRepository.save(p3);
			}
		};
	}
}
