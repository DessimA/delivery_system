package com.delivery.controller;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Lista todos os produtos (público)")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Busca um produto por ID (público)")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cria um novo produto (ADMIN, RESTAURANT)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@RequestPart("produto") ProdutoRequestDTO produtoDTO, @RequestPart(value = "imagem", required = false) MultipartFile imagem) {
        ProdutoResponseDTO novoProduto = produtoService.criarProduto(produtoDTO, imagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @Operation(summary = "Atualiza um produto existente (ADMIN, RESTAURANT)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoDTO);
        if (produtoAtualizado != null) {
            return ResponseEntity.ok(produtoAtualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Exclui um produto (ADMIN, RESTAURANT)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
