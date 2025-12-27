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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryServiceTest extends AbstractServiceTest {

    @Mock private DeliveryRepository deliveryRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private DeliveryMapper deliveryMapper;

    @InjectMocks private DeliveryService deliveryService;

    @Test
    @DisplayName("Deve aceitar uma entrega disponivel")
    void shouldAcceptDelivery() {
        // Given
        User courier = new User();
        courier.setId(2L);
        Delivery delivery = Delivery.builder().id(1L).status(DeliveryStatus.PENDENTE).build();

        mockAuthenticatedUser(courier);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(deliveryMapper.toResponseDTO(any())).thenReturn(mock(DeliveryResponseDTO.class));

        // When
        DeliveryResponseDTO result = deliveryService.acceptDelivery(1L);

        // Then
        assertThat(result).isNotNull();
        verify(deliveryRepository).save(any());
    }
}