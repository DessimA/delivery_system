package com.delivery.service;

import com.delivery.dto.PixResponseDTO;
import com.delivery.model.Order;
import com.delivery.model.OrderStatus;
import com.delivery.model.Payment;
import com.delivery.model.User;
import com.delivery.repository.OrderRepository;
import com.delivery.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private SecurityService securityService;

    @InjectMocks private PaymentService paymentService;

    @Captor private ArgumentCaptor<Payment> paymentCaptor;

    @Test
    @DisplayName("Deve gerar PIX com sucesso quando valor confere com pedido")
    void shouldGeneratePixWhenAmountMatches() {
        User user = User.builder().id(1L).build();
        Order order = Order.builder().id(1L).customerId(1L).totalValue(BigDecimal.valueOf(100.00)).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        PixResponseDTO result = paymentService.createPixPayment(1L, BigDecimal.valueOf(100.00), user);

        assertThat(result).isNotNull();
        assertThat(result.transactionId()).isNotBlank();
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Deve rejeitar PIX quando valor diverge do total do pedido")
    void shouldRejectPixWhenAmountMismatch() {
        User user = User.builder().id(1L).build();
        Order order = Order.builder().id(1L).customerId(1L).totalValue(BigDecimal.valueOf(100.00)).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.createPixPayment(1L, BigDecimal.valueOf(50.00), user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount does not match order total");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar PIX quando usuario nao e dono do pedido")
    void shouldRejectPixWhenNotOrderOwner() {
        User user = User.builder().id(2L).build();
        Order order = Order.builder().id(1L).customerId(1L).totalValue(BigDecimal.valueOf(100.00)).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.createPixPayment(1L, BigDecimal.valueOf(100.00), user))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Order does not belong to the authenticated user");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar status do pagamento quando usuario e dono")
    void shouldReturnStatusWhenOwner() {
        User user = User.builder().id(1L).build();
        Order order = Order.builder().id(1L).customerId(1L).build();
        Payment payment = Payment.builder().id(1L).order(order).status("PENDING").build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        String status = paymentService.getPaymentStatus(1L, user);

        assertThat(status).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Deve rejeitar status quando usuario nao e dono nem admin")
    void shouldRejectStatusWhenNotOwner() {
        User user = User.builder().id(2L).build();
        Order order = Order.builder().id(1L).customerId(1L).build();
        Payment payment = Payment.builder().id(1L).order(order).status("PENDING").build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(securityService.isAdmin(user)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.getPaymentStatus(1L, user))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Not authorized to view this payment");
    }

    @Test
    @DisplayName("Deve confirmar pagamento e atualizar status do pedido")
    void shouldConfirmPayment() {
        Order order = Order.builder().id(1L).status(OrderStatus.WAITING_PAYMENT).build();
        Payment payment = Payment.builder().id(1L).order(order).status("PENDING").transactionId("tx-123").build();

        when(paymentRepository.findByTransactionId("tx-123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        paymentService.confirmPayment("tx-123");

        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(order);
        assertThat(payment.getStatus()).isEqualTo("CONFIRMED");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }
}
