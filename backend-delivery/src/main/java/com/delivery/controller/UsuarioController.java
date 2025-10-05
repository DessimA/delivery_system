package com.delivery.controller;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.mapper.UsuarioMapper;
import com.delivery.model.Pessoa;
import com.delivery.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    private final PessoaService pessoaService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(PessoaService pessoaService, UsuarioMapper usuarioMapper) {
        this.pessoaService = pessoaService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registra um novo usuário (público)")
    public ResponseEntity<UsuarioResponseDTO> registrar(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO novoUsuario = pessoaService.criarUsuario(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping("/me")
    @Operation(summary = "Busca os dados do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuCadastro() {
        Pessoa pessoaLogada = getPessoaLogada();
        return ResponseEntity.ok(usuarioMapper.toResponseDTO(pessoaLogada));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza os dados do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuCadastro(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        Pessoa pessoaLogada = getPessoaLogada();
        UsuarioResponseDTO usuarioAtualizado = pessoaService.atualizarUsuario(pessoaLogada.getCodigo(), usuarioRequestDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    private Pessoa getPessoaLogada() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Pessoa pessoa = pessoaService.buscarPorEmail(email);
        if (pessoa == null) {
            throw new IllegalStateException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return pessoa;
    }
}