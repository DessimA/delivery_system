package com.delivery.service;

import com.delivery.dto.DeliveryResponseDTO;
import com.delivery.mapper.DeliveryMapper;
import com.delivery.model.Delivery;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.User;
import com.delivery.repository.DeliveryRepository;
import com.delivery.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryServiceTest extends AbstractServiceTest {

    @Mock private DeliveryRepository deliveryRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private DeliveryMapper deliveryMapper;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks private DeliveryService deliveryService;

    @Test
    @DisplayName("Deve aceitar uma entrega disponivel")
    void shouldAcceptDelivery() {
        User courier = new User();
        courier.setId(2L);
        Delivery delivery = Delivery.builder().id(1L).status(DeliveryStatus.PENDENTE).build();

        mockAuthenticatedUser(courier);
        when(deliveryRepository.acceptAtomically(eq(1L), any())).thenReturn(1);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryMapper.toResponseDTO(any())).thenReturn(mock(DeliveryResponseDTO.class));

        DeliveryResponseDTO result = deliveryService.acceptDelivery(1L);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve lancar erro se entrega ja foi aceita por outro entregador")
    void shouldThrowWhenDeliveryAlreadyAccepted() {
        User courier = new User();
        courier.setId(2L);

        mockAuthenticatedUser(courier);
        when(deliveryRepository.acceptAtomically(eq(1L), any())).thenReturn(0);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
            () -> deliveryService.acceptDelivery(1L));
    }
}