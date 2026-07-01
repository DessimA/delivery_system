package com.delivery.controller;

import com.delivery.dto.PixRequestDTO;
import com.delivery.dto.PixResponseDTO;
import com.delivery.model.User;
import com.delivery.service.PaymentService;
import com.delivery.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SecurityService securityService;

    @PostMapping("/pix/generate")
    public ResponseEntity<PixResponseDTO> generatePix(@Valid @RequestBody PixRequestDTO pixRequestDTO) {
        User user = securityService.getAuthenticatedUser();
        PixResponseDTO response = paymentService.createPixPayment(pixRequestDTO.orderId(), pixRequestDTO.amount(), user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        User user = securityService.getAuthenticatedUser();
        return ResponseEntity.ok(paymentService.getPaymentStatus(id, user));
    }

    @PostMapping("/confirm/{transactionId}")
    public ResponseEntity<Void> confirmPayment(@PathVariable String transactionId) {
        paymentService.confirmPayment(transactionId);
        return ResponseEntity.ok().build();
    }
}
