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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final EstablishmentRepository establishmentRepository;
    private final ProductMapper productMapper;
    private final SecurityService securityService;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder", e);
        }
    }

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
        if (securityService.isAdmin(user) && dto.establishmentId() != null) {
            establishment = establishmentRepository.findById(dto.establishmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento nao encontrado."));
        } else {
            establishment = user.getEstablishment();
            if (establishment == null) {
                // If admin is creating but didn't provide establishment ID, we might need a fallback or throw error.
                // Assuming for now Admin has a default establishment or this flow is for Restaurant Owners.
                // If user is ADMIN and no establishmentId provided, try to find one linked to the admin user (if any)
                if (securityService.isAdmin(user)) {
                     // For simplicity, let's allow Admin to create product without establishment if model allows,
                     // or enforce establishmentId for Admin.
                     // Here: checking if user has one.
                } 
                if (establishment == null) {
                     throw new IllegalStateException("Usuario nao tem estabelecimento e nenhum ID foi fornecido.");
                }
            }
        }
        product.setEstablishment(establishment);

        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            try {
                Path copyLocation = Paths.get(uploadDir + fileName);
                Files.copy(image.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Falha ao armazenar arquivo " + fileName, e);
            }
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