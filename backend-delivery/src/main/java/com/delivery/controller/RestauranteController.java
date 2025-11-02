package com.delivery.controller;

import com.delivery.dto.EstabelecimentoResponseDTO;

import com.delivery.dto.ProdutoRequestDTO;

import com.delivery.dto.ProdutoResponseDTO;

import com.delivery.mapper.EstabelecimentoMapper;

import com.delivery.service.EstabelecimentoService;

import com.delivery.service.ProdutoService;

import org.springframework.web.bind.annotation.*;



import java.util.List;



@RestController

@RequestMapping("/api/restaurante")

public class RestauranteController {



    private final ProdutoService produtoService;

    private final EstabelecimentoService estabelecimentoService;

    private final EstabelecimentoMapper estabelecimentoMapper;



    public RestauranteController(ProdutoService produtoService, EstabelecimentoService estabelecimentoService, EstabelecimentoMapper estabelecimentoMapper) {

        this.produtoService = produtoService;

        this.estabelecimentoService = estabelecimentoService;

        this.estabelecimentoMapper = estabelecimentoMapper;

    }



    @GetMapping("/meu-estabelecimento")

    public EstabelecimentoResponseDTO getMeuEstabelecimento() {

        return estabelecimentoMapper.toEstabelecimentoResponseDTO(estabelecimentoService.buscarMeuEstabelecimento());

    }



    @GetMapping("/meus-produtos")

    public List<ProdutoResponseDTO> getMeusProdutos() {

        return produtoService.listarDoMeuEstabelecimento();

    }



    @PostMapping("/produtos")

    public ProdutoResponseDTO criarProduto(@RequestBody ProdutoRequestDTO produtoRequestDTO) {

        return produtoService.criarProduto(produtoRequestDTO, null);

    }



    @PutMapping("/produtos/{id}")

    public ProdutoResponseDTO atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequestDTO produtoRequestDTO) {

        return produtoService.atualizarProduto(id, produtoRequestDTO);

    }



    @DeleteMapping("/produtos/{id}")

    public void deletarProduto(@PathVariable Long id) {

        produtoService.excluir(id);

    }

}
