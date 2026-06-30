package com.delivery.service;

import com.delivery.dto.OrderRequestDTO;
import com.delivery.dto.OrderResponseDTO;
import com.delivery.mapper.OrderMapper;
import com.delivery.model.Delivery;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.Establishment;
import com.delivery.model.Order;
import com.delivery.model.Product;
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

        validateSameEstablishment(products);

        Order order = Order.builder()
                .customerId(customerId)
                .deliveryAddress(request.deliveryAddress())
                .products(products)
                .orderDate(LocalDateTime.now())
                .status(DEFAULT_STATUS)
                .deliveryFee(DEFAULT_FEE)
                .build();

        order.calculateTotal();

        String originAddress = resolveOriginAddress(products);

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
        if (request.productIds() == null || request.productIds().isEmpty()) {
            throw new IllegalArgumentException("A lista de produtos nao pode estar vazia.");
        }
    }
}
