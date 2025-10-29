package com.delivery.service;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.mapper.UsuarioMapper;
import com.delivery.model.Usuario;
import com.delivery.model.Role;
import com.delivery.repository.UsuarioRepository;
import com.delivery.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.delivery.exception.EmailAlreadyExistsException;
import com.delivery.exception.UserNotFoundException;
import java.util.Collections;

@Service
public class UsuarioService {

    private static final String ROLE_USER = "USER";

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.findByEmail(usuarioRequestDTO.getEmail()) != null) {
            throw new EmailAlreadyExistsException("O e-mail informado já está em uso.");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioRequestDTO);
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        Role userRole = roleRepository.findByPapel("USER");
        usuario.setRoles(Collections.singletonList(userRole));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }



    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o ID: " + id));

        // TODO: Adicionar validação caso o e-mail esteja sendo alterado para um que já existe.
        if (!usuarioExistente.getEmail().equals(usuarioRequestDTO.getEmail()) && usuarioRepository.findByEmail(usuarioRequestDTO.getEmail()) != null) {
            throw new EmailAlreadyExistsException("O e-mail informado já está em uso.");
        }

        usuarioExistente.setNome(usuarioRequestDTO.getNome());
        usuarioExistente.setCpf(usuarioRequestDTO.getCpf());
        usuarioExistente.setDataNascimento(usuarioRequestDTO.getDataNascimento());
        usuarioExistente.setEndereco(usuarioRequestDTO.getEndereco());
        usuarioExistente.setEmail(usuarioRequestDTO.getEmail());

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    public UsuarioResponseDTO getUsuarioResponseDTO(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponseDTO)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o ID: " + id));
    }
}