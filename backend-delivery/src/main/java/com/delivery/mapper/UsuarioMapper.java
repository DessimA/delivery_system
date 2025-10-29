package com.delivery.mapper;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.model.Usuario;
import com.delivery.model.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setCpf(dto.getCpf());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setEndereco(dto.getEndereco());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        return usuario;
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setCodigo(usuario.getCodigo());
        dto.setNome(usuario.getNome());
        dto.setCpf(usuario.getCpf());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setEndereco(usuario.getEndereco());
        dto.setEmail(usuario.getEmail());
        if (usuario.getRoles() != null) {
            dto.setRoles(usuario.getRoles().stream()
                    .map(Role::getPapel)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
