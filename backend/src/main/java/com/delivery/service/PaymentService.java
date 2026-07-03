package com.delivery.service;

import com.delivery.dto.PixResponseDTO;
import com.delivery.exception.PaymentExpiredException;
import com.delivery.model.Payment;
import com.delivery.model.Order;
import com.delivery.model.OrderStatus;
import com.delivery.model.User;
import com.delivery.repository.PaymentRepository;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final SecurityService securityService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${payment.pix.expiration-minutes:30}")
    private int expirationMinutes;

    private static final String MOCK_COPY_PASTE = "00020126330014br.gov.bcb.pix0111+5511999999999520400005303986540510.005802BR5913NOME_RECEBEDOR6008CIDADE62070503***6304E2A4";

    @Transactional
    public PixResponseDTO createPixPayment(Long orderId, BigDecimal amount, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        validateOrderOwnership(order, user);

        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            if ("PENDING".equals(payment.getStatus())) {
                return buildResponse(payment);
            }
            if ("CONFIRMED".equals(payment.getStatus())) {
                throw new IllegalStateException("Order is already paid.");
            }
        }

        BigDecimal orderTotal = order.getTotalValue().setScale(2, RoundingMode.HALF_EVEN);
        if (amount.compareTo(orderTotal) != 0) {
            throw new IllegalArgumentException("Payment amount does not match order total.");
        }

        String transactionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(expirationMinutes);

        Payment payment = Payment.builder()
                .order(order)
                .amount(orderTotal)
                .transactionId(transactionId)
                .status("PENDING")
                .paymentDate(now)
                .expiresAt(expiresAt)
                .build();

        paymentRepository.save(payment);

        return buildResponse(payment);
    }

    public String getPaymentStatusByOrderId(Long orderId, User user) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for this order."));

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

        if ("CONFIRMED".equals(payment.getStatus())) {
            return;
        }

        if (payment.getExpiresAt() != null && LocalDateTime.now().isAfter(payment.getExpiresAt())) {
            throw new PaymentExpiredException("Payment link has expired.");
        }

        payment.setStatus("CONFIRMED");
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    private PixResponseDTO buildResponse(Payment payment) {
        String transactionId = payment.getTransactionId();
        String confirmationUrl = frontendUrl + "/payment/confirm/" + transactionId;
        String expiresAt = payment.getExpiresAt() != null ? payment.getExpiresAt().toString() : null;

        return new PixResponseDTO(
            confirmationUrl,
            MOCK_COPY_PASTE,
            transactionId,
            expiresAt,
            payment.getAmount()
        );
    }

    private void validateOrderOwnership(Order order, User user) {
        if (!Objects.equals(order.getCustomerId(), user.getId())) {
            throw new SecurityException("Order does not belong to the authenticated user.");
        }
    }
}
