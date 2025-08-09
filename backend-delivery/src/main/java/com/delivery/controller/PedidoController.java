package com.delivery.controller;

import com.delivery.dto.PedidoRequestDTO;
import com.delivery.dto.PedidoResponseDTO;
import com.delivery.model.Pessoa;
import com.delivery.service.PedidoService;
import com.delivery.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO pedidoDTO) {
        Pessoa pessoaLogada = getPessoaLogada();
        if (pessoaLogada == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PedidoResponseDTO novoPedido = pedidoService.criarPedido(pedidoDTO, pessoaLogada.getCodigo());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarMeusPedidos() {
        Pessoa pessoaLogada = getPessoaLogada();
        if (pessoaLogada == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PedidoResponseDTO> pedidos = pedidoService.listarPedidosPorCliente(pessoaLogada.getCodigo());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodosOsPedidos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        if (pedido != null) {
            // Adicionar verificação de segurança para garantir que o usuário só possa ver seus próprios pedidos
            return ResponseEntity.ok(pedido);
        }
        return ResponseEntity.notFound().build();
    }

    private Pessoa getPessoaLogada() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return pessoaService.buscarProEmail(email);
    }
}
