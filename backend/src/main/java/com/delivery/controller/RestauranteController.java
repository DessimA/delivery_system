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
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestauranteController {

    private final ProductService productService;
    private final EstablishmentService establishmentService;
    private final EstablishmentMapper establishmentMapper;

    @GetMapping("/my-establishment")
    public EstablishmentResponseDTO getMyEstablishment() {
        return establishmentMapper.toResponseDTO(establishmentService.findMyEstablishment());
    }

    @GetMapping("/my-products")
    public List<ProductResponseDTO> getMyProducts() {
        return productService.listMyProducts();
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO createProduct(
            @RequestPart("data") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.createProduct(dto, image);
    }

    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO updateProduct(
            @PathVariable Long id,
            @RequestPart("data") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.updateProduct(id, dto, image);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}
