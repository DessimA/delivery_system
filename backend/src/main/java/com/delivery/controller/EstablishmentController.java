package com.delivery.controller;

import com.delivery.dto.EstablishmentRequestDTO;
import com.delivery.dto.EstablishmentResponseDTO;
import com.delivery.dto.ProductResponseDTO;
import com.delivery.mapper.EstablishmentMapper;
import com.delivery.model.User;
import com.delivery.service.EstablishmentService;
import com.delivery.service.ProductService;
import com.delivery.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establishments")
@RequiredArgsConstructor
public class EstablishmentController {

    private final EstablishmentService establishmentService;
    private final ProductService productService;
    private final EstablishmentMapper establishmentMapper;
    private final SecurityService securityService;

    @GetMapping
    public List<EstablishmentResponseDTO> findAll() {
        return establishmentService.findAll().stream()
                .map(establishmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentResponseDTO> findById(@PathVariable Long id) {
        return establishmentService.findById(id)
                .map(establishmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/products")
    public List<ProductResponseDTO> findProductsByEstablishment(@PathVariable Long id) {
        return productService.listByEstablishment(id);
    }

    @PostMapping
    public ResponseEntity<EstablishmentResponseDTO> create(@Valid @RequestBody EstablishmentRequestDTO dto) {
        User user = securityService.getAuthenticatedUser();
        com.delivery.model.Establishment entity = establishmentMapper.toEntity(dto);
        entity.setUser(user);
        EstablishmentResponseDTO response = establishmentMapper.toResponseDTO(establishmentService.save(entity));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstablishmentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EstablishmentRequestDTO dto) {
        com.delivery.model.Establishment existing = establishmentService.findById(id)
                .orElseThrow(() -> new com.delivery.exception.ResourceNotFoundException("Establishment not found"));
        existing.setName(dto.name());
        existing.setCnpj(dto.cnpj());
        existing.setAddress(dto.address());
        existing.setPhone(dto.phone());
        EstablishmentResponseDTO response = establishmentMapper.toResponseDTO(establishmentService.save(existing));
        return ResponseEntity.ok(response);
    }
}
