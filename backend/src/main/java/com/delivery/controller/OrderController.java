package com.delivery.controller;

import com.delivery.dto.OrderRequestDTO;
import com.delivery.dto.OrderResponseDTO;
import com.delivery.model.User;
import com.delivery.service.OrderService;
import com.delivery.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityService securityService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        User user = securityService.getAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, user.getId()));
    }

    @GetMapping("/me")
    public ResponseEntity<java.util.List<OrderResponseDTO>> getMyOrders() {
        User user = securityService.getAuthenticatedUser();
        return ResponseEntity.ok(orderService.findMyOrders(user.getId()));
    }
}
