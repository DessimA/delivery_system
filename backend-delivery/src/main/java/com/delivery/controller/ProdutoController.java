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

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Lista todos os produtos (público)")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Busca um produto por ID (público)")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        // TODO: Mover a lógica de tratamento de 'não encontrado' para a camada de serviço,
        // lançando uma exceção (ex: ResourceNotFoundException) que pode ser tratada
        // globalmente por um @ControllerAdvice para retornar 404.
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cria um novo produto (ADMIN, RESTAURANT)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@RequestPart("produto") ProdutoRequestDTO produtoRequestDTO, @RequestPart(value = "imagem", required = false) MultipartFile imagem) {
        ProdutoResponseDTO novoProduto = produtoService.criarProduto(produtoRequestDTO, imagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @Operation(summary = "Atualiza um produto existente (ADMIN, RESTAURANT)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        // TODO: A lógica de 'não encontrado' também deveria ser tratada no serviço.
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoRequestDTO);
        if (produtoAtualizado != null) {
            return ResponseEntity.ok(produtoAtualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Exclui um produto (ADMIN, RESTAURANT)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        // TODO: O serviço de exclusão também deve lidar com o caso de o recurso não existir, possivelmente lançando uma exceção.
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
