package com.delivery.controller;

import com.delivery.dto.PedidoRequestDTO;
import com.delivery.dto.PedidoResponseDTO;
import com.delivery.model.Usuario;
import com.delivery.service.PedidoService;
import com.delivery.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo pedido para o usuário logado")
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        Usuario usuarioLogado = getUsuarioLogada();
        PedidoResponseDTO novoPedido = pedidoService.criarPedido(pedidoRequestDTO, usuarioLogado.getCodigo());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping("/meus-pedidos")
    @Operation(summary = "Lista os pedidos do usuário logado")
    public ResponseEntity<List<PedidoResponseDTO>> listarMeusPedidos() {
        Usuario usuarioLogado = getUsuarioLogada();
        List<PedidoResponseDTO> pedidos = pedidoService.listarPedidosPorCliente(usuarioLogado.getCodigo());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping
    @Operation(summary = "Lista todos os pedidos (Acesso para ADMIN)")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodosOsPedidos() {
        // TODO: Implementar autorização. Este endpoint deve ser acessível apenas por usuários com a role 'ADMIN'.
        // Ex: @PreAuthorize("hasRole('ADMIN')")
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pedido por ID")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        // TODO: Implementar autorização na camada de serviço.
        // O serviço deve garantir que um usuário só possa acessar seus próprios pedidos, a menos que seja um ADMIN.
        // Atualmente, qualquer usuário autenticado pode ver qualquer pedido.
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        if (pedido != null) {
            return ResponseEntity.ok(pedido);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Recupera a entidade Pessoa associada ao usuário autenticado na requisição.
     *
     * @return A entidade Pessoa do usuário logado.
     * @throws IllegalStateException se o usuário autenticado não for encontrado no banco de dados.
     */
    private Usuario getUsuarioLogada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            // Esta situação indica uma inconsistência de dados, pois um usuário autenticado
            // deve sempre ter um registro correspondente no banco de dados.
            throw new IllegalStateException("Inconsistência de dados: usuário autenticado não encontrado no sistema: " + email);
        }
        return usuario;
    }
}
