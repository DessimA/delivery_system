package com.delivery.controller;

import com.delivery.dto.EstablishmentResponseDTO;
import com.delivery.dto.ProductResponseDTO;
import com.delivery.mapper.EstablishmentMapper;
import com.delivery.service.EstablishmentService;
import com.delivery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
public class EstablishmentController {

    private final EstablishmentService establishmentService;
    private final ProductService productService;
    private final EstablishmentMapper establishmentMapper;

    @GetMapping
    public List<EstablishmentResponseDTO> listarTodos() {
        return establishmentService.findAll().stream()
                .map(establishmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentResponseDTO> buscarPorId(@PathVariable Long id) {
        return establishmentService.findById(id)
                .map(establishmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/produtos")
    public List<ProductResponseDTO> listarProdutosPorEstabelecimento(@PathVariable Long id) {
        return productService.listByEstablishment(id);
    }
}
