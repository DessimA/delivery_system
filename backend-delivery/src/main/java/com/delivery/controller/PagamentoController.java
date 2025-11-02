package com.delivery.controller;

import com.delivery.dto.PixRequestDTO;
import com.delivery.dto.PixResponseDTO;
import com.delivery.service.PagamentoPixService;
import com.delivery.service.PagamentoService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoPixService pagamentoPixService;
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoPixService pagamentoPixService, PagamentoService pagamentoService) {
        this.pagamentoPixService = pagamentoPixService;
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pix/gerar")
    public ResponseEntity<PixResponseDTO> gerarPix(@Valid @RequestBody PixRequestDTO pixRequestDTO) throws WriterException, IOException {
        String qrCode = pagamentoPixService.gerarQRCodePix(pixRequestDTO.getPedidoId(), pixRequestDTO.getValor());
        PixResponseDTO response = new PixResponseDTO();
        response.setQrCode(qrCode);
        response.setCopiaCola("00020126330014br.gov.bcb.pix0111+5511999999999520400005303986540510.005802BR5913NOME_RECEBEDOR6008CIDADE62070503***6304E2A4");
        response.setTransactionId(UUID.randomUUID().toString());
        response.setExpiraEm("2025-01-11T19:00:00");
        response.setValor(pixRequestDTO.getValor());

        pagamentoService.criarPagamento(pixRequestDTO.getPedidoId(), pixRequestDTO.getValor(), response.getTransactionId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.getStatusPagamento(id));
    }
}
