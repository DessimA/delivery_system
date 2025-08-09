package com.delivery.mapper;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.model.Pessoa;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Pessoa toEntity(UsuarioRequestDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setEndereco(dto.getEndereco());
        pessoa.setEmail(dto.getEmail());
        pessoa.setSenha(dto.getSenha());
        return pessoa;
    }

    public UsuarioResponseDTO toResponseDTO(Pessoa pessoa) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setCodigo(pessoa.getCodigo());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setEndereco(pessoa.getEndereco());
        dto.setEmail(pessoa.getEmail());
        return dto;
    }
}
