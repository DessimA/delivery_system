package com.delivery.service;

import com.delivery.exception.UserNotFoundException;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Usuario;
import com.delivery.repository.EstabelecimentoRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstabelecimentoServiceTest {

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EstabelecimentoService estabelecimentoService;

    private Estabelecimento estabelecimento1;
    private Estabelecimento estabelecimento2;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        estabelecimento1 = new Estabelecimento();
        estabelecimento1.setId(1L);
        estabelecimento1.setNome("Pizzaria Teste");
        estabelecimento1.setEndereco("Rua Teste, 123");

        estabelecimento2 = new Estabelecimento();
        estabelecimento2.setId(2L);
        estabelecimento2.setNome("Burger Teste");
        estabelecimento2.setEndereco("Av Teste, 456");

        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setEmail("user@test.com");
        usuario.setEstabelecimento(estabelecimento1);

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(usuario.getEmail());
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(usuario);
    }

    @Test
    void listarTodos_deveRetornarTodosEstabelecimentos() {
        when(estabelecimentoRepository.findAll()).thenReturn(Arrays.asList(estabelecimento1, estabelecimento2));

        List<Estabelecimento> result = estabelecimentoService.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(estabelecimentoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_deveRetornarEstabelecimentoQuandoEncontrado() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento1));

        Optional<Estabelecimento> result = estabelecimentoService.buscarPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(estabelecimento1.getNome(), result.get().getNome());
        verify(estabelecimentoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoEncontrado() {
        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Estabelecimento> result = estabelecimentoService.buscarPorId(99L);

        assertFalse(result.isPresent());
        verify(estabelecimentoRepository, times(1)).findById(99L);
    }

    @Test
    void salvar_deveSalvarNovoEstabelecimento() {
        Estabelecimento novoEstabelecimento = new Estabelecimento();
        novoEstabelecimento.setNome("Novo Estabelecimento");
        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenReturn(novoEstabelecimento);

        Estabelecimento result = estabelecimentoService.salvar(novoEstabelecimento);

        assertNotNull(result);
        assertEquals("Novo Estabelecimento", result.getNome());
        verify(estabelecimentoRepository, times(1)).save(novoEstabelecimento);
    }

    @Test
    void deletar_deveDeletarEstabelecimentoExistente() {
        doNothing().when(estabelecimentoRepository).deleteById(1L);

        estabelecimentoService.deletar(1L);

        verify(estabelecimentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void buscarMeuEstabelecimento_deveRetornarEstabelecimentoDoUsuarioLogado() {
        Estabelecimento result = estabelecimentoService.buscarMeuEstabelecimento();

        assertNotNull(result);
        assertEquals(estabelecimento1.getNome(), result.getNome());
    }

    @Test
    void buscarMeuEstabelecimento_deveLancarExcecaoSeUsuarioNaoTiverEstabelecimento() {
        usuario.setEstabelecimento(null);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            estabelecimentoService.buscarMeuEstabelecimento();
        });

        assertEquals("Usuário não tem um estabelecimento associado.", exception.getMessage());
    }
}
