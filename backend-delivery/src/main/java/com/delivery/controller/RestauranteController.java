package com.delivery.controller;

import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurante")
@Tag(name = "Restaurante", description = "Endpoints para gerenciamento do restaurante")
@SecurityRequirement(name = "bearerAuth")
public class RestauranteController {

    private final ProdutoService produtoService;

    public RestauranteController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/me/produtos")
    @Operation(summary = "Lista todos os produtos do estabelecimento do usuário logado (RESTAURANT)")
    public ResponseEntity<List<ProdutoResponseDTO>> listarMeusProdutos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarDoMeuEstabelecimento();
        return ResponseEntity.ok(produtos);
    }
}
