package com.delivery.service;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.mapper.UsuarioMapper;
import com.delivery.model.Pessoa;
import com.delivery.model.Role;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Pessoa buscarProEmail(String email) {
        return pessoaRepository.findByEmail(email);
    }

    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioDTO) {
        Pessoa pessoa = usuarioMapper.toEntity(usuarioDTO);
        pessoa.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        // Atribuir a role USER por padrão
        Role userRole = roleRepository.findByPapel("USER");
        if (userRole == null) {
            // Se a role USER não existir, cria ela (isso deve ser tratado na inicialização da app, mas é um fallback)
            userRole = new Role();
            userRole.setPapel("USER");
            roleRepository.save(userRole);
        }
        pessoa.setRoles(Collections.singletonList(userRole));

        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        return usuarioMapper.toResponseDTO(pessoaSalva);
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO usuarioDTO) {
        return pessoaRepository.findById(id).map(pessoaExistente -> {
            pessoaExistente.setNome(usuarioDTO.getNome());
            pessoaExistente.setCpf(usuarioDTO.getCpf());
            pessoaExistente.setDataNascimento(usuarioDTO.getDataNascimento());
            pessoaExistente.setEndereco(usuarioDTO.getEndereco());
            pessoaExistente.setEmail(usuarioDTO.getEmail());
            // A senha não é atualizada aqui para segurança
            Pessoa pessoaAtualizada = pessoaRepository.save(pessoaExistente);
            return usuarioMapper.toResponseDTO(pessoaAtualizada);
        }).orElse(null);
    }
}