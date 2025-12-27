package com.delivery.service;

import com.delivery.dto.OrderRequestDTO;
import com.delivery.dto.OrderResponseDTO;
import com.delivery.mapper.OrderMapper;
import com.delivery.model.Delivery;
import com.delivery.model.Order;
import com.delivery.model.Product;
import com.delivery.model.DeliveryStatus;
import com.delivery.repository.OrderRepository;
import com.delivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String DEFAULT_STATUS = "WAITING_PAYMENT";
    private static final float DEFAULT_FEE = 5.0f;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, Long customerId) {
        validateRequest(request);

        List<Product> products = productRepository.findAllById(request.productIds());
        
        Order order = Order.builder()
                .customerId(customerId)
                .deliveryAddress(request.deliveryAddress())
                .products(products)
                .orderDate(LocalDateTime.now())
                .status(DEFAULT_STATUS)
                .deliveryFee(DEFAULT_FEE)
                .build();

        order.calculateTotal();
        
        Delivery delivery = Delivery.builder()
                .order(order)
                .destinationAddress(order.getDeliveryAddress())
                .status(DeliveryStatus.PENDENTE)
                .createdAt(LocalDateTime.now())
                .build();
        order.setDelivery(delivery);

        return orderMapper.toResponseDTO(orderRepository.save(order));
    }

    private void validateRequest(OrderRequestDTO request) {
        if (request.productIds() == null || request.productIds().isEmpty()) {
            throw new IllegalArgumentException("A lista de produtos nao pode estar vazia.");
        }
    }
}
