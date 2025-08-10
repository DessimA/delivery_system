package com.delivery.controller;

import com.delivery.dto.LoginRequestDTO;
import com.delivery.dto.PagamentoRequestDTO;
import com.delivery.model.Pedido;
import com.delivery.model.Pessoa;
import com.delivery.model.Role;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.RoleRepository;
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

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PedidoRepository pedidoRepository;

    private String userToken;
    private Pedido testPedido;

    @BeforeEach
    void setUp() throws Exception {
        // Clear database for clean state (handled by @Transactional and test setup)
        pessoaRepository.deleteAll();
        roleRepository.deleteAll();
        pedidoRepository.deleteAll();

        // Create USER role
        Role userRole = new Role();
        userRole.setPapel("USER");
        roleRepository.save(userRole);

        // Create a test user
        Pessoa user = new Pessoa();
        user.setEmail("testuser@example.com");
        user.setSenha(passwordEncoder.encode("password"));
        user.setRoles(Collections.singletonList(userRole));
        pessoaRepository.save(user);

        // Get token for test user
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setSenha("password");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        userToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();

        // Create a test order
        testPedido = new Pedido();
        testPedido.setCodigoCliente(user.getCodigo());
        testPedido.setValorTotal(BigDecimal.valueOf(50.0).floatValue());
        testPedido.setEnderecoPedido("Rua do Pedido, 123");
        pedidoRepository.save(testPedido);
    }

    @Test
    void processarPagamento_shouldApprovePaymentForAuthenticatedUser() throws Exception {
        PagamentoRequestDTO pagamentoRequest = new PagamentoRequestDTO();
        pagamentoRequest.setCodigoPedido(testPedido.getCodigoPedido());
        pagamentoRequest.setMetodoPagamento("CREDIT_CARD");
        pagamentoRequest.setValor(BigDecimal.valueOf(50.0));
        pagamentoRequest.setNumeroCartao("1234567890123456");

        mockMvc.perform(post("/api/pagamentos/processar")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));
    }

    @Test
    void processarPagamento_shouldRejectPaymentForInvalidCard() throws Exception {
        PagamentoRequestDTO pagamentoRequest = new PagamentoRequestDTO();
        pagamentoRequest.setCodigoPedido(testPedido.getCodigoPedido());
        pagamentoRequest.setMetodoPagamento("CREDIT_CARD");
        pagamentoRequest.setValor(BigDecimal.valueOf(50.0));
        pagamentoRequest.setNumeroCartao("1111"); // Simula cartão recusado

        mockMvc.perform(post("/api/pagamentos/processar")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECUSADO"));
    }

    @Test
    void processarPagamento_shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        PagamentoRequestDTO pagamentoRequest = new PagamentoRequestDTO();
        pagamentoRequest.setCodigoPedido(testPedido.getCodigoPedido());
        pagamentoRequest.setMetodoPagamento("CREDIT_CARD");
        pagamentoRequest.setValor(BigDecimal.valueOf(50.0));
        pagamentoRequest.setNumeroCartao("1234567890123456");

        mockMvc.perform(post("/api/pagamentos/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagamentoRequest)))
                .andExpect(status().isUnauthorized());
    }
}
