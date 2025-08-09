package com.delivery.service;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.model.Entrega;
import com.delivery.model.Pedido;
import com.delivery.model.Pessoa;
import com.delivery.model.Role;
import com.delivery.repository.EntregaRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.PessoaRepository;
import com.delivery.mapper.EntregaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntregaServiceTest {

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private EntregaMapper entregaMapper;

    @InjectMocks
    private EntregaService entregaService;

    private Pessoa adminUser;
    private Pessoa deliveryUser;
    private Pedido pedido;
    private Entrega entregaPendente;
    private EntregaRequestDTO entregaRequestDTO;

    @BeforeEach
    void setUp() {
        // Mock SecurityContextHolder
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Setup common test data
        adminUser = new Pessoa();
        adminUser.setEmail("admin@test.com");
        Role adminRole = new Role();
        adminRole.setPapel("ADMIN");
        adminUser.setRoles(Collections.singletonList(adminRole));

        deliveryUser = new Pessoa();
        deliveryUser.setEmail("delivery@test.com");
        deliveryUser.setCodigo(2L);
        Role deliveryRole = new Role();
        deliveryRole.setPapel("DELIVERY");
        deliveryUser.setRoles(Collections.singletonList(deliveryRole));

        pedido = new Pedido();
        pedido.setCodigoPedido(1L);

        entregaPendente = new Entrega();
        entregaPendente.setId(1L);
        entregaPendente.setPedido(pedido);
        entregaPendente.setStatus("PENDENTE");

        entregaRequestDTO = new EntregaRequestDTO();
        entregaRequestDTO.setCodigoPedido(1L);
        entregaRequestDTO.setEnderecoDestino("Rua Teste, 123");
    }

    @Test
    void criarEntrega_shouldCreateNewDelivery() {
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.of(pedido));
        when(entregaMapper.toEntity(any(EntregaRequestDTO.class))).thenReturn(new Entrega());
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaPendente);
        when(entregaMapper.toResponseDTO(any(Entrega.class))).thenReturn(null); // Not testing DTO conversion here

        entregaService.criarEntrega(entregaRequestDTO);

        verify(entregaRepository, times(1)).save(any(Entrega.class));
    }

    @Test
    void criarEntrega_shouldThrowExceptionIfPedidoNotFound() {
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                entregaService.criarEntrega(entregaRequestDTO)
        );
    }

    @Test
    void criarEntrega_shouldThrowExceptionIfPedidoAlreadyHasDelivery() {
        pedido.setEntrega(new Entrega()); // Simulate existing delivery
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class, () ->
                entregaService.criarEntrega(entregaRequestDTO)
        );
    }

    @Test
    void listarEntregasDisponiveis_shouldReturnOnlyPendingAndUnassigned() {
        Entrega entregaAceita = new Entrega();
        entregaAceita.setId(2L);
        entregaAceita.setStatus("ACEITA");
        entregaAceita.setEntregador(deliveryUser);

        when(entregaRepository.findAll()).thenReturn(Arrays.asList(entregaPendente, entregaAceita));
        when(entregaMapper.toResponseDTO(any(Entrega.class))).thenReturn(null);

        List<EntregaResponseDTO> result = entregaService.listarEntregasDisponiveis();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void aceitarEntrega_shouldAssignDeliveryToDeliveryUser() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(deliveryUser.getEmail());
        when(pessoaRepository.findByEmail(deliveryUser.getEmail())).thenReturn(deliveryUser);
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.of(entregaPendente));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaPendente);
        when(entregaMapper.toResponseDTO(any(Entrega.class))).thenReturn(null);

        entregaService.aceitarEntrega(1L);

        verify(entregaRepository, times(1)).save(argThat(entrega ->
                "ACEITA".equals(entrega.getStatus()) && entrega.getEntregador().getCodigo().equals(deliveryUser.getCodigo())
        ));
    }

    @Test
    void aceitarEntrega_shouldThrowExceptionIfDeliveryNotFound() {
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                entregaService.aceitarEntrega(1L)
        );
    }

    @Test
    void aceitarEntrega_shouldThrowExceptionIfDeliveryNotPending() {
        entregaPendente.setStatus("ACEITA");
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.of(entregaPendente));

        assertThrows(IllegalStateException.class, () ->
                entregaService.aceitarEntrega(1L)
        );
    }

    @Test
    void aceitarEntrega_shouldThrowExceptionIfUserNotDeliveryRole() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(adminUser.getEmail());
        when(pessoaRepository.findByEmail(adminUser.getEmail())).thenReturn(adminUser);
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.of(entregaPendente));

        assertThrows(SecurityException.class, () ->
                entregaService.aceitarEntrega(1L)
        );
    }

    @Test
    void atualizarStatusEntrega_shouldAllowAdminToUpdateStatus() {
        Entrega entregaAceita = new Entrega();
        entregaAceita.setId(1L);
        entregaAceita.setStatus("ACEITA");
        entregaAceita.setEntregador(deliveryUser);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(adminUser.getEmail());
        when(pessoaRepository.findByEmail(adminUser.getEmail())).thenReturn(adminUser);
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.of(entregaAceita));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaAceita);
        when(entregaMapper.toResponseDTO(any(Entrega.class))).thenReturn(null);

        entregaService.atualizarStatusEntrega(1L, "EM_ROTA");

        verify(entregaRepository, times(1)).save(argThat(entrega ->
                "EM_ROTA".equals(entrega.getStatus())
        ));
    }

    @Test
    void atualizarStatusEntrega_shouldAllowAssignedDeliveryUserToUpdateStatus() {
        Entrega entregaAceita = new Entrega();
        entregaAceita.setId(1L);
        entregaAceita.setStatus("ACEITA");
        entregaAceita.setEntregador(deliveryUser);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(deliveryUser.getEmail());
        when(pessoaRepository.findByEmail(deliveryUser.getEmail())).thenReturn(deliveryUser);
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.of(entregaAceita));
        when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaAceita);
        when(entregaMapper.toResponseDTO(any(Entrega.class))).thenReturn(null);

        entregaService.atualizarStatusEntrega(1L, "EM_ROTA");

        verify(entregaRepository, times(1)).save(argThat(entrega ->
                "EM_ROTA".equals(entrega.getStatus())
        ));
    }

    @Test
    void atualizarStatusEntrega_shouldPreventUnassignedDeliveryUserFromUpdatingStatus() {
        Entrega entregaAceita = new Entrega();
        entregaAceita.setId(1L);
        entregaAceita.setStatus("ACEITA");
        Pessoa otherDeliveryUser = new Pessoa();
        otherDeliveryUser.setEmail("other@test.com");
        otherDeliveryUser.setCodigo(3L);
        Role deliveryRole = new Role();
        deliveryRole.setPapel("DELIVERY");
        otherDeliveryUser.setRoles(Collections.singletonList(deliveryRole));
        entregaAceita.setEntregador(otherDeliveryUser);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(deliveryUser.getEmail());
        when(pessoaRepository.findByEmail(deliveryUser.getEmail())).thenReturn(deliveryUser);
        when(entregaRepository.findById(anyLong())).thenReturn(Optional.of(entregaAceita));

        assertThrows(SecurityException.class, () ->
                entregaService.atualizarStatusEntrega(1L, "EM_ROTA")
        );
        verify(entregaRepository, never()).save(any(Entrega.class));
    }

    @Test
    void listarMinhasEntregas_shouldReturnOnlyAssignedDeliveries() {
        Entrega minhaEntrega = new Entrega();
        minhaEntrega.setId(1L);
        minhaEntrega.setEntregador(deliveryUser);
        minhaEntrega.setStatus("ACEITA");

        Entrega outraEntrega = new Entrega();
        outraEntrega.setId(2L);
        outraEntrega.setEntregador(adminUser);
        outraEntrega.setStatus("ACEITA");

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(deliveryUser.getEmail());
        when(pessoaRepository.findByEmail(deliveryUser.getEmail())).thenReturn(deliveryUser);
        when(entregaRepository.findByEntregador(deliveryUser)).thenReturn(Collections.singletonList(minhaEntrega));
        when(entregaMapper.toResponseDTO(any(Entrega.class))).thenReturn(null);

        List<EntregaResponseDTO> result = entregaService.listarMinhasEntregas();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(entregaRepository, times(1)).findByEntregador(deliveryUser);
    }
}
