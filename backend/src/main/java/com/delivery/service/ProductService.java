package com.delivery.service;

import com.delivery.dto.ProductRequestDTO;
import com.delivery.dto.ProductResponseDTO;
import com.delivery.exception.ResourceNotFoundException;
import com.delivery.mapper.ProductMapper;
import com.delivery.model.Establishment;
import com.delivery.model.Product;
import com.delivery.model.User;
import com.delivery.repository.EstablishmentRepository;
import com.delivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final EstablishmentRepository establishmentRepository;
    private final ProductMapper productMapper;
    private final SecurityService securityService;

    public List<ProductResponseDTO> listAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> listMyProducts() {
        User user = securityService.getAuthenticatedUser();
        Establishment establishment = user.getEstablishment();
        if (establishment == null) {
            throw new IllegalStateException("Usuario nao e dono de um estabelecimento.");
        }
        return productRepository.findByEstablishmentId(establishment.getId()).stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> listByEstablishment(Long establishmentId) {
        return productRepository.findByEstablishmentId(establishmentId).stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto, MultipartFile image) {
        Product product = productMapper.toEntity(dto);
        User user = securityService.getAuthenticatedUser();

        Establishment establishment;
        if (securityService.isAdmin(user)) {
            establishment = establishmentRepository.findById(dto.establishmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento nao encontrado."));
        } else {
            establishment = user.getEstablishment();
            if (establishment == null) {
                throw new IllegalStateException("Usuario nao tem estabelecimento.");
            }
        }
        product.setEstablishment(establishment);

        if (image != null && !image.isEmpty()) {
            product.setImageUrl(image.getOriginalFilename());
        }
        
        return productMapper.toResponseDTO(productRepository.save(product));
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado."));

        verifyAuthorization(product);

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        
        return productMapper.toResponseDTO(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado."));
        verifyAuthorization(product);
        productRepository.delete(product);
    }

    private void verifyAuthorization(@NonNull Product product) {
        User user = securityService.getAuthenticatedUser();
        if (securityService.isAdmin(user)) return;
        
        Establishment mine = user.getEstablishment();
        Establishment productEst = product.getEstablishment();
        
        if (mine == null || productEst == null || !Objects.equals(productEst.getId(), mine.getId())) {
            throw new SecurityException("Nao autorizado.");
        }
    }
}