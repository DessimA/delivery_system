package com.delivery.config;

import com.delivery.model.*;
import com.delivery.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ProdutoRepository produtoRepository;

    public DataLoader(RoleRepository roleRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EstabelecimentoRepository estabelecimentoRepository, ProdutoRepository produtoRepository) {
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Cria as roles se não existirem
        Role userRole = createRoleIfNotFound("USER");
        Role adminRole = createRoleIfNotFound("ADMIN");
        Role restaurantRole = createRoleIfNotFound("RESTAURANT");
        Role deliveryRole = createRoleIfNotFound("DELIVERY");

        // Cria o administrador padrão se não existir
        createAdminUserIfNotFound(adminRole);

        // Cria estabelecimentos, produtos e usuários de restaurante
        createPizzaria(restaurantRole);
        createBurgerKing(restaurantRole);
        createSushiHouse(restaurantRole);
        createCafeGourmet(restaurantRole);
        createRestauranteMineiro(restaurantRole);
    }

    private Role createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByPapel(roleName);
        if (role == null) {
            role = new Role();
            role.setPapel(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    private void createAdminUserIfNotFound(Role adminRole) {
        if (usuarioRepository.findByEmail("admin@fakedata.com") == null) {
            Usuario admin = new Usuario();
            admin.setNome("Admin Padrao");
            admin.setEmail("admin@fakedata.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setCpf("00000000000");
            admin.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
            admin.setEndereco("Endereço Administrativo");
            admin.setRoles(Collections.singletonList(adminRole));
            usuarioRepository.save(admin);
        }
    }

    private void createPizzaria(Role restaurantRole) {
        if (estabelecimentoRepository.findByNome("Pizzaria Bella Vista") == null) {
            Estabelecimento pizzaria = new Estabelecimento();
            pizzaria.setNome("Pizzaria Bella Vista");
            pizzaria.setEndereco("Rua da Pizza, 123");

            Usuario user = new Usuario();
            user.setNome("Dono Pizzaria");
            user.setEmail("pizzaria@fakedata.com");
            user.setSenha(passwordEncoder.encode("123456"));
            user.setRoles(Collections.singletonList(restaurantRole));
            user.setEstabelecimento(pizzaria);
            pizzaria.setUsuario(user); // Set the user on the establishment
            usuarioRepository.save(user);

            criarProduto("Pizza Calabresa", "Molho de tomate, calabresa, cebola e azeitona", 35.0f, "https://res.cloudinary.com/demo/image/upload/pizza.jpg", pizzaria);
            criarProduto("Pizza Margherita", "Molho de tomate, mussarela e manjericão", 30.0f, "https://res.cloudinary.com/demo/image/upload/pizza.jpg", pizzaria);
        }
    }

    private void createBurgerKing(Role restaurantRole) {
        if (estabelecimentoRepository.findByNome("Burger King Express") == null) {
            Estabelecimento burgerKing = new Estabelecimento();
            burgerKing.setNome("Burger King Express");
            burgerKing.setEndereco("Avenida do Burger, 456");

            Usuario user = new Usuario();
            user.setNome("Dono Burger King");
            user.setEmail("bk@fakedata.com");
            user.setSenha(passwordEncoder.encode("123456"));
            user.setRoles(Collections.singletonList(restaurantRole));
            user.setEstabelecimento(burgerKing);
            burgerKing.setUsuario(user); // Set the user on the establishment
            usuarioRepository.save(user);

            criarProduto("Whopper", "O clássico Whopper com queijo, picles, cebola, tomate e alface", 25.0f, "https://res.cloudinary.com/demo/image/upload/burger.jpg", burgerKing);
            criarProduto("Cheeseburger", "Pão, carne e queijo", 15.0f, "https://res.cloudinary.com/demo/image/upload/burger.jpg", burgerKing);
        }
    }

    private void createSushiHouse(Role restaurantRole) {
        if (estabelecimentoRepository.findByNome("Sushi House") == null) {
            Estabelecimento sushiHouse = new Estabelecimento();
            sushiHouse.setNome("Sushi House");
            sushiHouse.setEndereco("Rua do Sushi, 789");

            Usuario user = new Usuario();
            user.setNome("Dono Sushi House");
            user.setEmail("sushi@fakedata.com");
            user.setSenha(passwordEncoder.encode("123456"));
            user.setRoles(Collections.singletonList(restaurantRole));
            user.setEstabelecimento(sushiHouse);
            sushiHouse.setUsuario(user); // Set the user on the establishment
            usuarioRepository.save(user);

            criarProduto("Combinado Salmão", "20 peças de salmão", 50.0f, "https://res.cloudinary.com/demo/image/upload/sushi.jpg", sushiHouse);
            criarProduto("Hot Roll", "10 peças de hot roll", 20.0f, "https://res.cloudinary.com/demo/image/upload/sushi.jpg", sushiHouse);
        }
    }

    private void createCafeGourmet(Role restaurantRole) {
        if (estabelecimentoRepository.findByNome("Café Gourmet") == null) {
            Estabelecimento cafe = new Estabelecimento();
            cafe.setNome("Café Gourmet");
            cafe.setEndereco("Avenida do Café, 101");

            Usuario user = new Usuario();
            user.setNome("Dono Café");
            user.setEmail("cafe@fakedata.com");
            user.setSenha(passwordEncoder.encode("123456"));
            user.setRoles(Collections.singletonList(restaurantRole));
            user.setEstabelecimento(cafe);
            cafe.setUsuario(user); // Set the user on the establishment
            usuarioRepository.save(user);

            criarProduto("Café Expresso", "Café forte e encorpado", 5.0f, "https://res.cloudinary.com/demo/image/upload/coffee.jpg", cafe);
            criarProduto("Capuccino", "Café com leite e chocolate", 8.0f, "https://res.cloudinary.com/demo/image/upload/coffee.jpg", cafe);
        }
    }

    private void createRestauranteMineiro(Role restaurantRole) {
        if (estabelecimentoRepository.findByNome("Restaurante Mineiro") == null) {
            Estabelecimento mineiro = new Estabelecimento();
            mineiro.setNome("Restaurante Mineiro");
            mineiro.setEndereco("Rua da Comida Mineira, 202");

            Usuario user = new Usuario();
            user.setNome("Dono Mineiro");
            user.setEmail("mineiro@fakedata.com");
            user.setSenha(passwordEncoder.encode("123456"));
            user.setRoles(Collections.singletonList(restaurantRole));
            user.setEstabelecimento(mineiro);
            mineiro.setUsuario(user); // Set the user on the establishment
            usuarioRepository.save(user);

            criarProduto("Feijão Tropeiro", "Feijão, linguiça, torresmo, couve e ovo", 40.0f, "https://res.cloudinary.com/demo/image/upload/food.jpg", mineiro);
            criarProduto("Pão de Queijo", "O autêntico pão de queijo mineiro", 3.0f, "https://res.cloudinary.com/demo/image/upload/food.jpg", mineiro);
        }
    }

    private void criarProduto(String nome, String descricao, float preco, String caminhoImagem, Estabelecimento estabelecimento) {
        Produto produto = new Produto();
        produto.setNomeProduto(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setCaminhoImagem(caminhoImagem);
        produto.setEstabelecimento(estabelecimento);
        produtoRepository.save(produto);
    }
}
