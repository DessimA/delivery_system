package com.delivery.service;

import com.delivery.dto.DeliveryRequestDTO;
import com.delivery.dto.DeliveryResponseDTO;
import com.delivery.mapper.DeliveryMapper;
import com.delivery.model.Delivery;
import com.delivery.model.Order;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.User;
import com.delivery.repository.DeliveryRepository;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final String ROLE_DELIVERY = "ROLE_DELIVERY";

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;
    private final SecurityService securityService;

    @Transactional
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO dto) {
        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (order.getDelivery() != null) {
            throw new IllegalStateException("Order already has a delivery.");
        }

        Delivery delivery = Delivery.builder()
                .order(order)
                .status(DeliveryStatus.PENDENTE)
                .originAddress(dto.originAddress())
                .destinationAddress(dto.destinationAddress())
                .createdAt(LocalDateTime.now())
                .build();

        return deliveryMapper.toResponseDTO(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponseDTO> listAvailable() {
        return deliveryRepository.findByStatusAndCourierIsNull(DeliveryStatus.PENDENTE).stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryResponseDTO acceptDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found."));

        if (delivery.getStatus() != DeliveryStatus.PENDENTE || delivery.getCourier() != null) {
            throw new IllegalStateException("Delivery unavailable.");
        }

        securityService.verifyRole(ROLE_DELIVERY);
        User courier = securityService.getAuthenticatedUser();

        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ACEITA);
        
        return deliveryMapper.toResponseDTO(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponseDTO updateStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found."));

        verifyAuthorization(delivery);
        
        delivery.setStatus(newStatus);
        if (newStatus == DeliveryStatus.ENTREGUE) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }

        return deliveryMapper.toResponseDTO(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponseDTO> listMyDeliveries() {
        securityService.verifyRole(ROLE_DELIVERY);
        User courier = securityService.getAuthenticatedUser();
        return deliveryRepository.findByCourier(courier).stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void verifyAuthorization(Delivery delivery) {
        User user = securityService.getAuthenticatedUser();
        boolean isAdmin = securityService.isAdmin(user);
        boolean isOwner = delivery.getCourier() != null && delivery.getCourier().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Not authorized.");
        }
    }
}
