package com.delivery.service;

import com.delivery.dto.PixResponseDTO;
import com.delivery.model.Payment;
import com.delivery.model.Order;
import com.delivery.model.OrderStatus;
import com.delivery.model.User;
import com.delivery.repository.PaymentRepository;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final SecurityService securityService;

    private static final String MOCK_QR_CODE = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";
    private static final String MOCK_COPY_PASTE = "00020126330014br.gov.bcb.pix0111+5511999999999520400005303986540510.005802BR5913NOME_RECEBEDOR6008CIDADE62070503***6304E2A4";

    @Transactional
    public PixResponseDTO createPixPayment(Long orderId, BigDecimal amount, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        validateOrderOwnership(order, user);

        BigDecimal orderTotal = order.getTotalValue().setScale(2, RoundingMode.HALF_EVEN);
        if (amount.compareTo(orderTotal) != 0) {
            throw new IllegalArgumentException("Payment amount does not match order total.");
        }

        String transactionId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .order(order)
                .amount(orderTotal)
                .transactionId(transactionId)
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        String expiresAt = LocalDateTime.now().plusMinutes(30).toString();

        return new PixResponseDTO(
            MOCK_QR_CODE,
            MOCK_COPY_PASTE,
            transactionId,
            expiresAt,
            orderTotal
        );
    }

    public String getPaymentStatus(Long id, User user) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));

        Order order = payment.getOrder();
        if (!Objects.equals(order.getCustomerId(), user.getId()) && !securityService.isAdmin(user)) {
            throw new SecurityException("Not authorized to view this payment.");
        }

        return payment.getStatus();
    }

    @Transactional
    public void confirmPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));

        payment.setStatus("CONFIRMED");
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    private void validateOrderOwnership(Order order, User user) {
        if (!Objects.equals(order.getCustomerId(), user.getId())) {
            throw new SecurityException("Order does not belong to the authenticated user.");
        }
    }
}
