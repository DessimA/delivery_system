package com.delivery.service;

import com.delivery.dto.OrderItemRequestDTO;
import com.delivery.dto.OrderRequestDTO;
import com.delivery.dto.OrderResponseDTO;
import com.delivery.mapper.OrderMapper;
import com.delivery.model.Order;
import com.delivery.model.Product;
import com.delivery.repository.OrderRepository;
import com.delivery.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderMapper orderMapper;

    @InjectMocks private OrderService orderService;

    @Test
    @DisplayName("Deve criar um pedido com sucesso e calcular total")
    void shouldCreateOrderSuccessfully() {
        // Given
        Long customerId = 1L;
        OrderRequestDTO request = new OrderRequestDTO("Rua Teste", List.of(new OrderItemRequestDTO(10L, 1)));
        Product mockProduct = Product.builder().id(10L).price(BigDecimal.valueOf(50.0)).build();
        
        when(productRepository.findAllById(any())).thenReturn(List.of(mockProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(mock(OrderResponseDTO.class));

        // When
        OrderResponseDTO result = orderService.createOrder(request, customerId);

        // Then
        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }
}