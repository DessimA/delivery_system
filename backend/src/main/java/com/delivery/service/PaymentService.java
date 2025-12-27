package com.delivery.service;

import com.delivery.model.Payment;
import com.delivery.model.Order;
import com.delivery.repository.PaymentRepository;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createPayment(Long orderId, double amount, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .transactionId(transactionId)
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
    }

    public String getPaymentStatus(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
        return payment.getStatus();
    }

    @Transactional
    public void confirmPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));

        payment.setStatus("CONFIRMED");
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus("PAID");
        orderRepository.save(order);
    }
}
