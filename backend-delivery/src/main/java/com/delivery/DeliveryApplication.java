package com.delivery;

import com.delivery.model.Estabelecimento;
import com.delivery.model.Pessoa;
import com.delivery.model.Produto;
import com.delivery.model.Role;
import com.delivery.repository.EstabelecimentoRepository;
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
    public CommandLineRunner demoData(PessoaRepository pessoaRepository, RoleRepository roleRepository, ProdutoRepository produtoRepository, EstabelecimentoRepository estabelecimentoRepository, PasswordEncoder passwordEncoder) {
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

            Role restaurantRole = roleRepository.findByPapel("RESTAURANT");
            if (restaurantRole == null) {
                restaurantRole = new Role();
                restaurantRole.setPapel("RESTAURANT");
                roleRepository.save(restaurantRole);
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

            // Criar estabelecimento e usuário restaurante padrão
            if (estabelecimentoRepository.count() == 0) {
                Estabelecimento est1 = new Estabelecimento();
                est1.setNome("Pizzaria do Zé");
                est1.setCnpj("12345678000199");

                Pessoa restaurantUser = new Pessoa();
                restaurantUser.setNome("Zé da Pizza");
                restaurantUser.setEmail("restaurant@example.com");
                restaurantUser.setSenha(passwordEncoder.encode("restaurant123"));
                restaurantUser.setRoles(Collections.singletonList(restaurantRole));
                restaurantUser.setEstabelecimento(est1);
                est1.setUsuario(restaurantUser);

                pessoaRepository.save(restaurantUser); // Salva Pessoa e Estabelecimento em cascata

                // Criar produtos fictícios para o estabelecimento
                if (produtoRepository.count() == 0) {
                    Produto p1 = new Produto();
                    p1.setNomeProduto("Pizza Calabresa");
                    p1.setDescricao("Pizza com molho de tomate, calabresa e queijo.");
                    p1.setPreco(45.00f);
                    p1.setCaminhoImagem("pizza_calabresa.png");
                    p1.setEstabelecimento(est1);
                    produtoRepository.save(p1);

                    Produto p2 = new Produto();
                    p2.setNomeProduto("Hamburguer X-Tudo");
                    p2.setDescricao("Pão, carne, queijo, bacon, ovo, alface e tomate.");
                    p2.setPreco(30.00f);
                    p2.setCaminhoImagem("hamburguer_x_tudo.png");
                    p2.setEstabelecimento(est1);
                    produtoRepository.save(p2);

                    Produto p3 = new Produto();
                    p3.setNomeProduto("Batata Frita");
                    p3.setDescricao("Porção de batatas fritas crocantes.");
                    p3.setPreco(15.00f);
                    p3.setCaminhoImagem("batata_frita.png");
                    p3.setEstabelecimento(est1);
                    produtoRepository.save(p3);
                }
            }
        };
    }
}
