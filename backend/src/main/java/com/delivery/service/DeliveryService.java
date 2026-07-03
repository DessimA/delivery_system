package com.delivery.service;

import com.delivery.dto.DeliveryResponseDTO;
import com.delivery.event.DeliveryUpdatedEvent;
import com.delivery.mapper.DeliveryMapper;
import com.delivery.model.Delivery;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.Order;
import com.delivery.model.OrderStatus;
import com.delivery.model.User;
import com.delivery.repository.DeliveryRepository;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final String ROLE_DELIVERY = "ROLE_DELIVERY";

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;
    private final SecurityService securityService;
    private final ApplicationEventPublisher eventPublisher;

    public List<DeliveryResponseDTO> listAvailable() {
        return deliveryRepository.findByStatusAndCourierIsNull(DeliveryStatus.PENDENTE).stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryResponseDTO acceptDelivery(Long id) {
        securityService.verifyRole(ROLE_DELIVERY);
        User courier = securityService.getAuthenticatedUser();

        int updated = deliveryRepository.acceptAtomically(id, courier);
        if (updated == 0) {
            throw new IllegalStateException("Delivery unavailable or already accepted by another courier.");
        }

        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Delivery not found after atomic update."));

        eventPublisher.publishEvent(new DeliveryUpdatedEvent(delivery));
        return deliveryMapper.toResponseDTO(delivery);
    }

    private static final Map<DeliveryStatus, Set<DeliveryStatus>> VALID_TRANSITIONS = Map.of(
        DeliveryStatus.PENDENTE, Set.of(DeliveryStatus.ACEITA, DeliveryStatus.CANCELADA),
        DeliveryStatus.ACEITA, Set.of(DeliveryStatus.COLETADA, DeliveryStatus.CANCELADA),
        DeliveryStatus.COLETADA, Set.of(DeliveryStatus.EM_ROTA, DeliveryStatus.CANCELADA),
        DeliveryStatus.EM_ROTA, Set.of(DeliveryStatus.ENTREGUE, DeliveryStatus.CANCELADA),
        DeliveryStatus.ENTREGUE, Set.of(),
        DeliveryStatus.CANCELADA, Set.of()
    );

    @Transactional
    public DeliveryResponseDTO updateStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found."));

        verifyAuthorization(delivery);

        Set<DeliveryStatus> allowed = VALID_TRANSITIONS.get(delivery.getStatus());
        if (allowed == null || !allowed.contains(newStatus)) {
            throw new IllegalStateException("Cannot transition from " + delivery.getStatus() + " to " + newStatus);
        }

        delivery.setStatus(newStatus);
        syncOrderStatus(delivery, newStatus);

        Delivery saved = deliveryRepository.save(delivery);
        eventPublisher.publishEvent(new DeliveryUpdatedEvent(saved));
        return deliveryMapper.toResponseDTO(saved);
    }

    public List<DeliveryResponseDTO> listMyDeliveries() {
        securityService.verifyRole(ROLE_DELIVERY);
        User courier = securityService.getAuthenticatedUser();
        return deliveryRepository.findByCourier(courier).stream()
                .map(deliveryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void syncOrderStatus(Delivery delivery, DeliveryStatus deliveryStatus) {
        Order order = delivery.getOrder();
        switch (deliveryStatus) {
            case ACEITA -> order.setStatus(OrderStatus.PREPARING);
            case EM_ROTA -> order.setStatus(OrderStatus.IN_TRANSIT);
            case ENTREGUE -> {
                order.setStatus(OrderStatus.DELIVERED);
                delivery.setDeliveredAt(LocalDateTime.now());
            }
            default -> {}
        }
        orderRepository.save(order);
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
