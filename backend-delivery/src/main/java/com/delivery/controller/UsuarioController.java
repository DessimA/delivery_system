package com.delivery.controller;

import com.delivery.dto.UsuarioRequestDTO;
import com.delivery.dto.UsuarioResponseDTO;
import com.delivery.mapper.UsuarioMapper;
import com.delivery.model.Usuario;
import com.delivery.service.UsuarioService;
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

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registra um novo usuário (público)")
    public ResponseEntity<UsuarioResponseDTO> registrar(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO novoUsuario = usuarioService.criarUsuario(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping("/me")
    @Operation(summary = "Busca os dados do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuCadastro() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(usuarioMapper.toResponseDTO(usuarioLogado));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza os dados do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuCadastro(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuarioLogado = getUsuarioLogado();
        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(usuarioLogado.getCodigo(), usuarioRequestDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            throw new IllegalStateException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return usuario;
    }
}