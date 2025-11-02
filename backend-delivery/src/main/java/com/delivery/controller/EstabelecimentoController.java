package com.delivery.controller;

import com.delivery.dto.EstabelecimentoRequestDTO;
import com.delivery.dto.EstabelecimentoResponseDTO;
import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.mapper.EstabelecimentoMapper;
import com.delivery.model.Estabelecimento;
import com.delivery.service.EstabelecimentoService;
import com.delivery.service.ProdutoService;
import jakarta.validation.Valid;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;
    private final EstabelecimentoMapper estabelecimentoMapper;
    private final ProdutoService produtoService;

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService, EstabelecimentoMapper estabelecimentoMapper, ProdutoService produtoService) {
        this.estabelecimentoService = estabelecimentoService;
        this.estabelecimentoMapper = estabelecimentoMapper;
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<EstabelecimentoResponseDTO> listarTodos() {
        return estabelecimentoService.listarTodos().stream()
                .map(estabelecimentoMapper::toEstabelecimentoResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return estabelecimentoService.buscarPorId(id)
                .map(estabelecimentoMapper::toEstabelecimentoResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public EstabelecimentoResponseDTO criar(@Valid @RequestBody EstabelecimentoRequestDTO dto) {
        Estabelecimento estabelecimento = estabelecimentoMapper.toEstabelecimento(dto);
        return estabelecimentoMapper.toEstabelecimentoResponseDTO(estabelecimentoService.salvar(estabelecimento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EstabelecimentoRequestDTO dto) {
        return estabelecimentoService.buscarPorId(id)
                .map(estabelecimentoExistente -> {
                    Estabelecimento estabelecimento = estabelecimentoMapper.toEstabelecimento(dto);
                    estabelecimento.setId(id);
                    return ResponseEntity.ok(estabelecimentoMapper.toEstabelecimentoResponseDTO(estabelecimentoService.salvar(estabelecimento)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return estabelecimentoService.buscarPorId(id)
                .map(estabelecimento -> {
                    estabelecimentoService.deletar(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/produtos")
    public List<ProdutoResponseDTO> listarProdutosPorEstabelecimento(@PathVariable Long id) {
        return produtoService.listarPorEstabelecimento(id);
    }
}
