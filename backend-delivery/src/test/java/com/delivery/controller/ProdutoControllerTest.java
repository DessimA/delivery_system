package com.delivery.controller;

import com.delivery.dto.LoginRequestDTO;
import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Produto;
import com.delivery.model.Role;
import com.delivery.model.Usuario;
import com.delivery.repository.EstabelecimentoRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
public class ProdutoControllerTest {

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
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    private String restaurantToken;
    private Estabelecimento estabelecimento;

    @BeforeEach
    void setUp() throws Exception {
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
        produtoRepository.deleteAll();
        estabelecimentoRepository.deleteAll();

        Role restaurantRole = new Role();
        restaurantRole.setPapel("ROLE_RESTAURANT");
        roleRepository.save(restaurantRole);

        Usuario restaurantUser = new Usuario();
        restaurantUser.setEmail("restaurant@example.com");
        restaurantUser.setSenha(passwordEncoder.encode("password"));
        restaurantUser.setRoles(Collections.singletonList(restaurantRole));

        estabelecimento = new Estabelecimento();
        estabelecimento.setNome("Restaurante Teste");
        estabelecimento.setUsuario(restaurantUser);
        restaurantUser.setEstabelecimento(estabelecimento);

        usuarioRepository.save(restaurantUser);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("restaurant@example.com");
        loginRequest.setSenha("password");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        restaurantToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    void criarProduto_shouldCreateProductSuccessfully() throws Exception {
        ProdutoRequestDTO produtoRequest = new ProdutoRequestDTO();
        produtoRequest.setNomeProduto("Novo Produto");
        produtoRequest.setPreco(15.0f);

        MockMultipartFile produtoJson = new MockMultipartFile("produto", "", "application/json", objectMapper.writeValueAsBytes(produtoRequest));

        mockMvc.perform(multipart("/api/produtos")
                        .file(produtoJson)
                        .header("Authorization", "Bearer " + restaurantToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeProduto").value("Novo Produto"));
    }

    @Test
    void listarProdutos_shouldReturnListOfProducts() throws Exception {
        Produto produto = new Produto();
        produto.setNomeProduto("Produto de Teste");
        produto.setPreco(10.0f);
        produto.setEstabelecimento(estabelecimento);
        produtoRepository.save(produto);

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].nomeProduto").value("Produto de Teste"));
    }
}
