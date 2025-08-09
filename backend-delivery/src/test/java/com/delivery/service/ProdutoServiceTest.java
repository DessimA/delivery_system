package com.delivery.service;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Pessoa;
import com.delivery.model.Produto;
import com.delivery.model.Role;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.mapper.ProdutoMapper;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Pessoa adminUser;
    private Pessoa restaurantUser;
    private Estabelecimento estabelecimento;
    private Produto produtoDoRestaurante;
    private ProdutoRequestDTO produtoRequestDTO;

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

        estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("Restaurante Teste");

        restaurantUser = new Pessoa();
        restaurantUser.setEmail("restaurant@test.com");
        Role restaurantRole = new Role();
        restaurantRole.setPapel("RESTAURANT");
        restaurantUser.setRoles(Collections.singletonList(restaurantRole));
        restaurantUser.setEstabelecimento(estabelecimento);

        produtoDoRestaurante = new Produto();
        produtoDoRestaurante.setIdProduto(1L);
        produtoDoRestaurante.setEstabelecimento(estabelecimento);

        produtoRequestDTO = new ProdutoRequestDTO();
        produtoRequestDTO.setNomeProduto("Novo Produto");
        produtoRequestDTO.setPreco(10.0f);
    }

    @Test
    void criarProduto_shouldAssociateWithRestaurantUser() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(restaurantUser.getEmail());
        when(pessoaRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoMapper.toEntity(any(ProdutoRequestDTO.class))).thenReturn(new Produto());
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoDoRestaurante);
        when(produtoMapper.toResponseDTO(any(Produto.class))).thenReturn(null); // Not testing DTO conversion here

        produtoService.criarProduto(produtoRequestDTO, null);

        verify(produtoRepository, times(1)).save(argThat(produto ->
                produto.getEstabelecimento().getId().equals(estabelecimento.getId())
        ));
    }

    @Test
    void atualizarProduto_shouldAllowAdminToUpdateAnyProduct() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(adminUser.getEmail());
        when(pessoaRepository.findByEmail(adminUser.getEmail())).thenReturn(adminUser);
        when(produtoRepository.findById(produtoDoRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDoRestaurante));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoDoRestaurante);

        produtoService.atualizarProduto(produtoDoRestaurante.getIdProduto(), produtoRequestDTO);

        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void atualizarProduto_shouldAllowRestaurantUserToUpdateOwnProduct() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(restaurantUser.getEmail());
        when(pessoaRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findById(produtoDoRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDoRestaurante));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoDoRestaurante);

        produtoService.atualizarProduto(produtoDoRestaurante.getIdProduto(), produtoRequestDTO);

        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void atualizarProduto_shouldPreventRestaurantUserFromUpdatingOtherProduct() {
        Estabelecimento outroEstabelecimento = new Estabelecimento();
        outroEstabelecimento.setId(2L);
        Produto produtoDeOutroRestaurante = new Produto();
        produtoDeOutroRestaurante.setIdProduto(2L);
        produtoDeOutroRestaurante.setEstabelecimento(outroEstabelecimento);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(restaurantUser.getEmail());
        when(pessoaRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findById(produtoDeOutroRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDeOutroRestaurante));

        assertThrows(SecurityException.class, () ->
                produtoService.atualizarProduto(produtoDeOutroRestaurante.getIdProduto(), produtoRequestDTO)
        );
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void excluir_shouldAllowAdminToDeleteAnyProduct() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(adminUser.getEmail());
        when(pessoaRepository.findByEmail(adminUser.getEmail())).thenReturn(adminUser);
        when(produtoRepository.findById(produtoDoRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDoRestaurante));

        produtoService.excluir(produtoDoRestaurante.getIdProduto());

        verify(produtoRepository, times(1)).delete(produtoDoRestaurante);
    }

    @Test
    void excluir_shouldAllowRestaurantUserToDeleteOwnProduct() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(restaurantUser.getEmail());
        when(pessoaRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findById(produtoDoRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDoRestaurante));

        produtoService.excluir(produtoDoRestaurante.getIdProduto());

        verify(produtoRepository, times(1)).delete(produtoDoRestaurante);
    }

    @Test
    void excluir_shouldPreventRestaurantUserFromDeletingOtherProduct() {
        Estabelecimento outroEstabelecimento = new Estabelecimento();
        outroEstabelecimento.setId(2L);
        Produto produtoDeOutroRestaurante = new Produto();
        produtoDeOutroRestaurante.setIdProduto(2L);
        produtoDeOutroRestaurante.setEstabelecimento(outroEstabelecimento);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(restaurantUser.getEmail());
        when(pessoaRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findById(produtoDeOutroRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDeOutroRestaurante));

        assertThrows(SecurityException.class, () ->
                produtoService.excluir(produtoDeOutroRestaurante.getIdProduto())
        );
        verify(produtoRepository, never()).delete(any(Produto.class));
    }

    @Test
    void listarDoMeuEstabelecimento_shouldReturnOnlyOwnProducts() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mock(UserDetails.class));
        when(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).thenReturn(restaurantUser.getEmail());
        when(pessoaRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findByEstabelecimentoId(estabelecimento.getId())).thenReturn(Collections.singletonList(produtoDoRestaurante));
        when(produtoMapper.toResponseDTO(any(Produto.class))).thenReturn(null); // Not testing DTO conversion here

        produtoService.listarDoMeuEstabelecimento();

        verify(produtoRepository, times(1)).findByEstabelecimentoId(estabelecimento.getId());
    }
}
