package com.delivery.controller;

import com.delivery.dto.PixRequestDTO;
import com.delivery.dto.PixResponseDTO;
import com.delivery.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pix/generate")
    public ResponseEntity<PixResponseDTO> generatePix(@Valid @RequestBody PixRequestDTO pixRequestDTO) {
        String qrCode = "mock-qr-code-base64-string";
        
        PixResponseDTO response = new PixResponseDTO(
            qrCode,
            "00020126330014br.gov.bcb.pix0111+5511999999999520400005303986540510.005802BR5913NOME_RECEBEDOR6008CIDADE62070503***6304E2A4",
            UUID.randomUUID().toString(),
            "2025-12-27T19:00:00",
            pixRequestDTO.amount()
        );

        paymentService.createPayment(pixRequestDTO.orderId(), pixRequestDTO.amount(), response.transactionId());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(id));
    }
}
