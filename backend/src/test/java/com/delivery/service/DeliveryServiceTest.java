package com.delivery.service;

import com.delivery.dto.DeliveryResponseDTO;
import com.delivery.event.DeliveryUpdatedEvent;
import com.delivery.mapper.DeliveryMapper;
import com.delivery.model.Delivery;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.Order;
import com.delivery.model.User;
import com.delivery.repository.DeliveryRepository;
import com.delivery.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DeliveryServiceTest extends AbstractServiceTest {

    @Mock private DeliveryRepository deliveryRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private DeliveryMapper deliveryMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

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

    @Test
    @DisplayName("Deve permitir transicao valida PENDENTE -> CANCELADA")
    void shouldAllowValidTransitionToCancelled() {
        User courier = new User();
        courier.setId(2L);
        Delivery delivery = Delivery.builder().id(1L).status(DeliveryStatus.PENDENTE).build();

        mockAuthenticatedUser(courier);
        mockIsAdmin(courier, true);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(deliveryMapper.toResponseDTO(any())).thenReturn(mock(DeliveryResponseDTO.class));

        deliveryService.updateStatus(1L, DeliveryStatus.CANCELADA);

        verify(deliveryRepository).save(argThat(d -> d.getStatus() == DeliveryStatus.CANCELADA));
    }

    @Test
    @DisplayName("Deve rejeitar transicao invalida PENDENTE -> ENTREGUE")
    void shouldRejectInvalidTransition() {
        User courier = new User();
        courier.setId(2L);
        Delivery delivery = Delivery.builder().id(1L).status(DeliveryStatus.PENDENTE).build();

        mockAuthenticatedUser(courier);
        mockIsAdmin(courier, true);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.updateStatus(1L, DeliveryStatus.ENTREGUE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot transition");
    }

    @Test
    @DisplayName("Deve completar ciclo completo de estado")
    void shouldCompleteFullStateCycle() {
        User courier = new User();
        courier.setId(2L);
        Order order = Order.builder().id(1L).build();
        Delivery delivery = Delivery.builder().id(1L).order(order).status(DeliveryStatus.ACEITA).build();

        mockAuthenticatedUser(courier);
        mockIsAdmin(courier, true);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(deliveryMapper.toResponseDTO(any())).thenReturn(mock(DeliveryResponseDTO.class));

        deliveryService.updateStatus(1L, DeliveryStatus.COLETADA);
        deliveryService.updateStatus(1L, DeliveryStatus.EM_ROTA);
        deliveryService.updateStatus(1L, DeliveryStatus.ENTREGUE);

        verify(deliveryRepository, times(3)).save(any());
        verify(eventPublisher, times(3)).publishEvent(any(DeliveryUpdatedEvent.class));
    }
}