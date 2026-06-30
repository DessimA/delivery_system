package com.delivery.service;

import com.delivery.dto.ProductRequestDTO;
import com.delivery.dto.ProductResponseDTO;
import com.delivery.mapper.ProductMapper;
import com.delivery.model.Establishment;
import com.delivery.model.Product;
import com.delivery.model.User;
import com.delivery.repository.EstablishmentRepository;
import com.delivery.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest extends AbstractServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private EstablishmentRepository establishmentRepository;
    @Mock private ProductMapper productMapper;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks private ProductService productService;

    private User admin;
    private User owner;
    private Product product;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
            productRepository, 
            establishmentRepository, 
            productMapper, 
            securityService,
            fileStorageService
        );

        Establishment est = new Establishment();
        est.setId(1L);

        admin = new User();
        admin.setId(99L);

        owner = new User();
        owner.setId(1L);
        owner.setEstablishment(est);

        product = new Product();
        product.setId(10L);
        product.setEstablishment(est);
    }

    @Test
    void shouldFindById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(mock(ProductResponseDTO.class));

        ProductResponseDTO result = productService.findById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldUpdateProductWhenAdmin() {
        mockAuthenticatedUser(admin);
        mockIsAdmin(admin, true);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateProduct(10L, new ProductRequestDTO("N", "D", 10.0, 1L), null);

        verify(productRepository).save(any(Product.class));
    }
}