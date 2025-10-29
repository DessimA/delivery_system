package com.delivery.service;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.exception.EmailAlreadyExistsException;
import com.delivery.mapper.UsuarioMapper;
import com.delivery.model.Role;
import com.delivery.model.Usuario;
import com.delivery.repository.RoleRepository;
import com.delivery.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO usuarioRequestDTO;
    private Usuario usuario;
    private Role role;

    @BeforeEach
    void setUp() {
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setNome("Test User");
        usuarioRequestDTO.setEmail("test@test.com");
        usuarioRequestDTO.setSenha("password");

        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setNome("Test User");
        usuario.setEmail("test@test.com");

        role = new Role();
        role.setPapel("ROLE_USER");
    }

    @Test
    void criarUsuario_shouldCreateUser_whenEmailIsNew() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(null);
        when(roleRepository.findByPapel(anyString())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioMapper.toEntity(any(UsuarioRequestDTO.class))).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(any(Usuario.class))).thenReturn(new UsuarioResponseDTO());

        UsuarioResponseDTO result = usuarioService.criarUsuario(usuarioRequestDTO);

        assertNotNull(result);
    }

    @Test
    void criarUsuario_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(usuario);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            usuarioService.criarUsuario(usuarioRequestDTO);
        });
    }
}
