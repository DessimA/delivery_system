package com.delivery.service;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.exception.UserNotFoundException;
import com.delivery.mapper.EntregaMapper;
import com.delivery.model.Entrega;
import com.delivery.model.Pedido;
import com.delivery.model.StatusEntrega;
import com.delivery.model.Usuario;
import com.delivery.repository.EntregaRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntregaServiceTest {

    @Mock
    private EntregaRepository entregaRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EntregaMapper entregaMapper;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private EntregaService entregaService;

    private Pedido pedido;
    private Entrega entregaPendente;
    private Entrega entregaAceita;
    private Usuario entregador;
    private Usuario admin;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setCodigoPedido(1L);

        entregador = new Usuario();
        entregador.setCodigo(2L);
        entregador.setEmail("entregador@test.com");
        entregador.setRoles(Collections.singletonList(new com.delivery.model.Role() {{ setPapel("DELIVERY"); }}));

        admin = new Usuario();
        admin.setCodigo(3L);
        admin.setEmail("admin@test.com");
        admin.setRoles(Collections.singletonList(new com.delivery.model.Role() {{ setPapel("ADMIN"); }}));

        entregaPendente = new Entrega();
        entregaPendente.setId(1L);
        entregaPendente.setPedido(pedido);
        entregaPendente.setStatus(StatusEntrega.PENDENTE);
        entregaPendente.setCriadoEm(new Date());

        entregaAceita = new Entrega();
        entregaAceita.setId(2L);
        entregaAceita.setPedido(pedido);
        entregaAceita.setStatus(StatusEntrega.ACEITA);
        entregaAceita.setEntregador(entregador);
        entregaAceita.setCriadoEm(new Date());

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthenticatedUser(Usuario user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(usuarioRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    void criarEntrega_deveCriarEntregaComStatusPendente() {
        EntregaRequestDTO requestDTO = new EntregaRequestDTO();
        requestDTO.setCodigoPedido(1L);

        EntregaResponseDTO responseDTO = new EntregaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(StatusEntrega.PENDENTE.name());

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(entregaMapper.toEntity(requestDTO)).thenReturn(entregaPendente);
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaPendente);
        when(entregaMapper.toResponseDTO(entregaPendente)).thenReturn(responseDTO);

        EntregaResponseDTO result = entregaService.criarEntrega(requestDTO);

        assertNotNull(result);
        assertEquals(StatusEntrega.PENDENTE.name(), result.getStatus());
        verify(entregaRepository, times(1)).save(any(Entrega.class));
        verify(webSocketService, times(1)).notifyDeliveryAvailable(any(Entrega.class));
    }

    @Test
    void listarEntregasDisponiveis_deveRetornarEntregasPendentesSemEntregador() {
        EntregaResponseDTO responseDTO = new EntregaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(StatusEntrega.PENDENTE.name());

        when(entregaRepository.findByStatusAndEntregadorIsNull(StatusEntrega.PENDENTE)).thenReturn(Arrays.asList(entregaPendente));
        when(entregaMapper.toResponseDTO(entregaPendente)).thenReturn(responseDTO);

        List<EntregaResponseDTO> result = entregaService.listarEntregasDisponiveis();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(StatusEntrega.PENDENTE.name(), result.get(0).getStatus());
        verify(entregaRepository, times(1)).findByStatusAndEntregadorIsNull(StatusEntrega.PENDENTE);
    }

    @Test
    void aceitarEntrega_deveAceitarEntregaPorEntregador() {
        mockAuthenticatedUser(entregador);

        EntregaResponseDTO responseDTO = new EntregaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(StatusEntrega.ACEITA.name());

        when(entregaRepository.findById(1L)).thenReturn(Optional.of(entregaPendente));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaAceita);
        when(entregaMapper.toResponseDTO(entregaAceita)).thenReturn(responseDTO);

        EntregaResponseDTO result = entregaService.aceitarEntrega(1L);

        assertNotNull(result);
        assertEquals(StatusEntrega.ACEITA.name(), result.getStatus());
        assertEquals(entregador.getCodigo(), entregaAceita.getEntregador().getCodigo());
        verify(entregaRepository, times(1)).save(any(Entrega.class));
        verify(webSocketService, times(1)).notifyDeliveryStatusUpdate(any(Entrega.class));
    }

    @Test
    void atualizarStatusEntrega_deveAtualizarStatusParaColetada() {
        mockAuthenticatedUser(entregador);

        Entrega entregaEmAceita = new Entrega();
        entregaEmAceita.setId(2L);
        entregaEmAceita.setPedido(pedido);
        entregaEmAceita.setStatus(StatusEntrega.ACEITA);
        entregaEmAceita.setEntregador(entregador);

        Entrega entregaColetada = new Entrega();
        entregaColetada.setId(2L);
        entregaColetada.setPedido(pedido);
        entregaColetada.setStatus(StatusEntrega.COLETADA);
        entregaColetada.setEntregador(entregador);

        EntregaResponseDTO responseDTO = new EntregaResponseDTO();
        responseDTO.setId(2L);
        responseDTO.setStatus(StatusEntrega.COLETADA.name());

        when(entregaRepository.findById(2L)).thenReturn(Optional.of(entregaEmAceita));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaColetada);
        when(entregaMapper.toResponseDTO(entregaColetada)).thenReturn(responseDTO);

        EntregaResponseDTO result = entregaService.atualizarStatusEntrega(2L, StatusEntrega.COLETADA);

        assertNotNull(result);
        assertEquals(StatusEntrega.COLETADA.name(), result.getStatus());
        verify(entregaRepository, times(1)).save(any(Entrega.class));
        verify(webSocketService, times(1)).notifyDeliveryStatusUpdate(any(Entrega.class));
    }

    @Test
    void atualizarStatusEntrega_deveLancarExcecaoParaTransicaoInvalida() {
        mockAuthenticatedUser(entregador);

        Entrega entregaEmAceita = new Entrega();
        entregaEmAceita.setId(2L);
        entregaEmAceita.setPedido(pedido);
        entregaEmAceita.setStatus(StatusEntrega.ACEITA);
        entregaEmAceita.setEntregador(entregador);

        when(entregaRepository.findById(2L)).thenReturn(Optional.of(entregaEmAceita));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entregaService.atualizarStatusEntrega(2L, StatusEntrega.PENDENTE);
        });

        assertEquals("Não é possível alterar o status de ACEITA para PENDENTE", exception.getMessage());
    }

    @Test
    void listarMinhasEntregas_deveRetornarEntregasDoEntregadorLogado() {
        mockAuthenticatedUser(entregador);

        EntregaResponseDTO responseDTO = new EntregaResponseDTO();
        responseDTO.setId(2L);
        responseDTO.setStatus(StatusEntrega.ACEITA.name());

        when(entregaRepository.findByEntregador(entregador)).thenReturn(Arrays.asList(entregaAceita));
        when(entregaMapper.toResponseDTO(entregaAceita)).thenReturn(responseDTO);

        List<EntregaResponseDTO> result = entregaService.listarMinhasEntregas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(StatusEntrega.ACEITA.name(), result.get(0).getStatus());
        verify(entregaRepository, times(1)).findByEntregador(entregador);
    }
}
