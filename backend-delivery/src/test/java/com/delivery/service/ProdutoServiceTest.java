package com.delivery.service;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Usuario;
import com.delivery.model.Produto;
import com.delivery.model.Role;
import com.delivery.repository.UsuarioRepository;
import com.delivery.repository.ProdutoRepository;
import com.delivery.mapper.ProdutoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Usuario adminUser;
    private Usuario restaurantUser;
    private Estabelecimento estabelecimento;
    private Produto produtoDoRestaurante;
    private ProdutoRequestDTO produtoRequestDTO;

    @BeforeEach
    void setUp() {
        adminUser = new Usuario();
        adminUser.setEmail("admin@test.com");
        Role adminRole = new Role();
        adminRole.setPapel("ROLE_ADMIN");
        adminUser.setRoles(Collections.singletonList(adminRole));

        estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setNome("Restaurante Teste");

        restaurantUser = new Usuario();
        restaurantUser.setEmail("restaurant@test.com");
        Role restaurantRole = new Role();
        restaurantRole.setPapel("ROLE_RESTAURANT");
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
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(restaurantUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) restaurantUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
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
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(adminUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) adminUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(adminUser.getEmail())).thenReturn(adminUser);
        when(produtoRepository.findById(produtoDoRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDoRestaurante));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoDoRestaurante);

        produtoService.atualizarProduto(produtoDoRestaurante.getIdProduto(), produtoRequestDTO);

        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void atualizarProduto_shouldAllowRestaurantUserToUpdateOwnProduct() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(restaurantUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) restaurantUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
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

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(restaurantUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) restaurantUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findById(produtoDeOutroRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDeOutroRestaurante));

        assertThrows(SecurityException.class, () ->
                produtoService.atualizarProduto(produtoDeOutroRestaurante.getIdProduto(), produtoRequestDTO)
        );
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void excluir_shouldAllowAdminToDeleteAnyProduct() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(adminUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) adminUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(adminUser.getEmail())).thenReturn(adminUser);
        when(produtoRepository.findById(produtoDoRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDoRestaurante));

        produtoService.excluir(produtoDoRestaurante.getIdProduto());

        verify(produtoRepository, times(1)).delete(produtoDoRestaurante);
    }

    @Test
    void excluir_shouldAllowRestaurantUserToDeleteOwnProduct() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(restaurantUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) restaurantUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
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

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(restaurantUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) restaurantUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findById(produtoDeOutroRestaurante.getIdProduto())).thenReturn(Optional.of(produtoDeOutroRestaurante));

        assertThrows(SecurityException.class, () ->
                produtoService.excluir(produtoDeOutroRestaurante.getIdProduto())
        );
        verify(produtoRepository, never()).delete(any(Produto.class));
    }

    @Test
    void listarDoMeuEstabelecimento_shouldReturnOnlyOwnProducts() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(restaurantUser.getEmail());
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) restaurantUser.getRoles());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(restaurantUser.getEmail())).thenReturn(restaurantUser);
        when(produtoRepository.findByEstabelecimentoId(estabelecimento.getId())).thenReturn(Collections.singletonList(produtoDoRestaurante));
        when(produtoMapper.toResponseDTO(any(Produto.class))).thenReturn(null); // Not testing DTO conversion here

        produtoService.listarDoMeuEstabelecimento();

        verify(produtoRepository, times(1)).findByEstabelecimentoId(estabelecimento.getId());
    }
}