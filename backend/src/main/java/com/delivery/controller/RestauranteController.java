package com.delivery.controller;

import com.delivery.dto.EstablishmentResponseDTO;
import com.delivery.dto.ProductRequestDTO;
import com.delivery.dto.ProductResponseDTO;
import com.delivery.mapper.EstablishmentMapper;
import com.delivery.service.EstablishmentService;
import com.delivery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/produtos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO criarProduto(
            @RequestPart("data") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.createProduct(dto, image);
    }

    @PutMapping(value = "/produtos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO atualizarProduto(
            @PathVariable Long id,
            @RequestPart("data") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.updateProduct(id, dto, image);
    }

    @DeleteMapping("/produtos/{id}")
    public void deletarProduto(@PathVariable Long id) {
        productService.delete(id);
    }
}
