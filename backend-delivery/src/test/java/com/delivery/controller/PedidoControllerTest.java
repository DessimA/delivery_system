package com.delivery.controller;

import com.delivery.dto.LoginRequestDTO;
import com.delivery.dto.PedidoRequestDTO;
import com.delivery.model.Pedido;
import com.delivery.model.Produto;
import com.delivery.model.Role;
import com.delivery.model.Usuario;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.repository.RoleRepository;
import com.delivery.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private String userToken;
    private Produto produto;
    private Usuario user;

    @BeforeEach
    void setUp() throws Exception {
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();

        Role userRole = new Role();
        userRole.setPapel("USER");
        roleRepository.save(userRole);

        user = new Usuario();
        user.setEmail("user@example.com");
        user.setSenha(passwordEncoder.encode("password"));
        user.setRoles(Collections.singletonList(userRole));
        usuarioRepository.save(user);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("user@example.com");
        loginRequest.setSenha("password");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        userToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();

        produto = new Produto();
        produto.setNomeProduto("Produto Teste");
        produto.setPreco(20.0f);
        produtoRepository.save(produto);
    }

    @Test
    void criarPedido_shouldCreateOrderSuccessfully() throws Exception {
        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setProdutoIds(Collections.singletonList(produto.getIdProduto()));
        pedidoRequest.setEnderecoPedido("Rua Teste, 456");

        mockMvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enderecoPedido").value("Rua Teste, 456"));
    }

    @Test
    void listarMeusPedidos_shouldReturnClientOrders() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setCodigoCliente(user.getCodigo());
        pedido.setEnderecoPedido("Meu Endereco");
        pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/meus-pedidos")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].enderecoPedido").value("Meu Endereco"));
    }
}
