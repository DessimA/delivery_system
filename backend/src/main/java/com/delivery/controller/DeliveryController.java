package com.delivery.controller;

import com.delivery.dto.DeliveryResponseDTO;
import com.delivery.model.DeliveryStatus;
import com.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/available")
    public List<DeliveryResponseDTO> listAvailable() {
        return deliveryService.listAvailable();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<DeliveryResponseDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.acceptDelivery(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateStatus(@PathVariable Long id, @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }

    @GetMapping("/mine")
    public List<DeliveryResponseDTO> listMine() {
        return deliveryService.listMyDeliveries();
    }
}