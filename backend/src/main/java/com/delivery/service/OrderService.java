package com.delivery.service;

import com.delivery.dto.OrderItemRequestDTO;
import com.delivery.dto.OrderRequestDTO;
import com.delivery.dto.OrderResponseDTO;
import com.delivery.mapper.OrderMapper;
import com.delivery.exception.ResourceNotFoundException;
import com.delivery.model.*;
import com.delivery.repository.OrderRepository;
import com.delivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal DEFAULT_FEE = BigDecimal.valueOf(5.00);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, Long customerId) {
        validateRequest(request);

        List<Long> productIds = request.items().stream()
                .map(OrderItemRequestDTO::productId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != java.util.Set.copyOf(productIds).size()) {
            throw new ResourceNotFoundException("One or more products not found.");
        }

        validateSameEstablishment(products);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        String originAddress = resolveOriginAddress(products);

        Order order = Order.builder()
                .customerId(customerId)
                .deliveryAddress(request.deliveryAddress())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.WAITING_PAYMENT)
                .deliveryFee(DEFAULT_FEE)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDTO itemRequest : request.items()) {
            Product product = productMap.get(itemRequest.productId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found: " + itemRequest.productId());
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        order.calculateTotal();

        Delivery delivery = Delivery.builder()
                .order(order)
                .originAddress(originAddress)
                .destinationAddress(order.getDeliveryAddress())
                .status(DeliveryStatus.PENDENTE)
                .createdAt(LocalDateTime.now())
                .build();
        order.setDelivery(delivery);

        return orderMapper.toResponseDTO(orderRepository.save(order));
    }

    private void validateSameEstablishment(List<Product> products) {
        if (products == null || products.isEmpty()) return;
        Long firstEstId = products.get(0).getEstablishment() != null
                ? products.get(0).getEstablishment().getId() : null;
        for (Product p : products) {
            Long estId = p.getEstablishment() != null ? p.getEstablishment().getId() : null;
            if (!java.util.Objects.equals(firstEstId, estId)) {
                throw new IllegalArgumentException("Todos os produtos devem pertencer ao mesmo estabelecimento.");
            }
        }
    }

    private String resolveOriginAddress(List<Product> products) {
        if (products == null || products.isEmpty()) return null;
        Establishment establishment = products.get(0).getEstablishment();
        if (establishment != null) return establishment.getAddress();
        return null;
    }

    public List<OrderResponseDTO> findMyOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private void validateRequest(OrderRequestDTO request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("A lista de itens nao pode estar vazia.");
        }
    }
}
