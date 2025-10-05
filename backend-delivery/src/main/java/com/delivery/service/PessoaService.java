package com.delivery.service;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.mapper.UsuarioMapper;
import com.delivery.model.Pessoa;
import com.delivery.model.Role;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PessoaService {

    private static final String ROLE_USER = "USER";

    private final PessoaRepository pessoaRepository;
    private final RoleRepository roleRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public PessoaService(PessoaRepository pessoaRepository, RoleRepository roleRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.pessoaRepository = pessoaRepository;
        this.roleRepository = roleRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Pessoa buscarPorEmail(String email) {
        return pessoaRepository.findByEmail(email);
    }

    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (pessoaRepository.findByEmail(usuarioRequestDTO.getEmail()) != null) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }

        Pessoa pessoa = usuarioMapper.toEntity(usuarioRequestDTO);
        pessoa.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));

        Role userRole = findOrCreateUserRole();
        pessoa.setRoles(Collections.singletonList(userRole));

        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        return usuarioMapper.toResponseDTO(pessoaSalva);
    }

    private Role findOrCreateUserRole() {
        // TODO: A criação de roles deve ser gerenciada por um processo de inicialização do sistema (ex: CommandLineRunner).
        Role userRole = roleRepository.findByPapel(ROLE_USER);
        if (userRole == null) {
            userRole = new Role();
            userRole.setPapel(ROLE_USER);
            return roleRepository.save(userRole);
        }
        return userRole;
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Pessoa pessoaExistente = pessoaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        // TODO: Adicionar validação caso o e-mail esteja sendo alterado para um que já existe.

        pessoaExistente.setNome(usuarioRequestDTO.getNome());
        pessoaExistente.setCpf(usuarioRequestDTO.getCpf());
        pessoaExistente.setDataNascimento(usuarioRequestDTO.getDataNascimento());
        pessoaExistente.setEndereco(usuarioRequestDTO.getEndereco());
        pessoaExistente.setEmail(usuarioRequestDTO.getEmail());

        Pessoa pessoaAtualizada = pessoaRepository.save(pessoaExistente);
        return usuarioMapper.toResponseDTO(pessoaAtualizada);
    }

    public UsuarioResponseDTO getUsuarioResponseDTO(Long id) {
        return pessoaRepository.findById(id)
                .map(usuarioMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }
}