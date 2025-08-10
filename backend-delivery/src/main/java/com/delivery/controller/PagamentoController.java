package com.delivery.controller;

import com.delivery.dto.PagamentoRequestDTO;
import com.delivery.dto.PagamentoResponseDTO;
import com.delivery.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "Endpoints para processamento de pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/processar")
    @Operation(summary = "Processa um pagamento para um pedido (USER)")
    public ResponseEntity<PagamentoResponseDTO> processarPagamento(@RequestBody PagamentoRequestDTO pagamentoDTO) {
        PagamentoResponseDTO response = pagamentoService.processarPagamento(pagamentoDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
