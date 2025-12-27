package com.delivery.controller;

import com.delivery.dto.EstablishmentResponseDTO;
import com.delivery.dto.ProductRequestDTO;
import com.delivery.dto.ProductResponseDTO;
import com.delivery.mapper.EstablishmentMapper;
import com.delivery.service.EstablishmentService;
import com.delivery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurante")
@RequiredArgsConstructor
public class RestauranteController {

    private final ProductService productService;
    private final EstablishmentService establishmentService;
    private final EstablishmentMapper establishmentMapper;

    @GetMapping("/meu-estabelecimento")
    public EstablishmentResponseDTO getMeuEstabelecimento() {
        return establishmentMapper.toResponseDTO(establishmentService.findMyEstablishment());
    }

    @GetMapping("/meus-produtos")
    public List<ProductResponseDTO> getMeusProdutos() {
        return productService.listMyProducts();
    }

    @PostMapping("/produtos")
    public ProductResponseDTO criarProduto(@RequestBody ProductRequestDTO productRequestDTO) {
        return productService.createProduct(productRequestDTO, null);
    }

    @PutMapping("/produtos/{id}")
    public ProductResponseDTO atualizarProduto(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.updateProduct(id, productRequestDTO);
    }

    @DeleteMapping("/produtos/{id}")
    public void deletarProduto(@PathVariable Long id) {
        productService.delete(id);
    }
}
