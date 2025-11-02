package com.delivery.controller;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.service.EntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.delivery.model.StatusEntrega;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
@Tag(name = "Entregas", description = "Endpoints para gerenciamento de entregas")
@SecurityRequirement(name = "bearerAuth")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @PostMapping
    @Operation(summary = "Cria uma nova entrega (ADMIN)")
    public ResponseEntity<EntregaResponseDTO> criarEntrega(@RequestBody EntregaRequestDTO entregaRequestDTO) {
        EntregaResponseDTO novaEntrega = entregaService.criarEntrega(entregaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaEntrega);
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Lista entregas disponíveis para entregadores (DELIVERY)")
    public ResponseEntity<List<EntregaResponseDTO>> listarEntregasDisponiveis() {
        List<EntregaResponseDTO> entregas = entregaService.listarEntregasDisponiveis();
        return ResponseEntity.ok(entregas);
    }

    @PostMapping("/{id}/aceitar")
    @Operation(summary = "Entregador aceita uma entrega (DELIVERY)")
    public ResponseEntity<EntregaResponseDTO> aceitarEntrega(@PathVariable Long id) {
        EntregaResponseDTO entregaAtualizada = entregaService.aceitarEntrega(id);
        return ResponseEntity.ok(entregaAtualizada);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de uma entrega (ADMIN, DELIVERY)")
    public ResponseEntity<EntregaResponseDTO> atualizarStatusEntrega(@PathVariable Long id, @RequestParam String novoStatus) {
        StatusEntrega statusEnum = StatusEntrega.valueOf(novoStatus.toUpperCase());
        EntregaResponseDTO entregaAtualizada = entregaService.atualizarStatusEntrega(id, statusEnum);
        return ResponseEntity.ok(entregaAtualizada);
    }

    @GetMapping("/minhas")
    @Operation(summary = "Lista as entregas atribuídas ao entregador logado (DELIVERY)")
    public ResponseEntity<List<EntregaResponseDTO>> listarMinhasEntregas() {
        List<EntregaResponseDTO> entregas = entregaService.listarMinhasEntregas();
        return ResponseEntity.ok(entregas);
    }
}
