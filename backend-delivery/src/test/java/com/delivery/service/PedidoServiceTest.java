package com.delivery.service;

import com.delivery.dto.PedidoRequestDTO;
import com.delivery.dto.PedidoResponseDTO;
import com.delivery.model.Pedido;
import com.delivery.model.Produto;
import com.delivery.model.Usuario;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.repository.UsuarioRepository;
import com.delivery.mapper.PedidoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario clientUser;
    private PedidoRequestDTO pedidoRequestDTO;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        clientUser = new Usuario();
        clientUser.setEmail("client@test.com");
        clientUser.setCodigo(1L);

        pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setProdutoIds(Collections.singletonList(1L));
        pedidoRequestDTO.setEnderecoPedido("Rua Teste, 123");

        produto = new Produto();
        produto.setIdProduto(1L);
        produto.setPreco(10.0f);

        pedido = new Pedido();
        pedido.setCodigoPedido(1L);
        pedido.setCodigoCliente(1L);
    }

    @Test
    void criarPedido_shouldCreateNewOrder() {
        when(produtoRepository.findAllById(any())).thenReturn(Collections.singletonList(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(new Pedido());
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        PedidoResponseDTO result = pedidoService.criarPedido(pedidoRequestDTO, 1L);

        assertNotNull(result);
    }

    @Test
    void listarPedidosPorCliente_shouldReturnClientOrders() {
        when(pedidoRepository.findByCodigoCliente(anyLong())).thenReturn(Collections.singletonList(pedido));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO());

        List<PedidoResponseDTO> result = pedidoService.listarPedidosPorCliente(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
