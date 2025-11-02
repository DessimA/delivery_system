package com.delivery.service;

import com.delivery.dto.PedidoRequestDTO;
import com.delivery.dto.PedidoResponseDTO;
import com.delivery.mapper.PedidoMapper;
import com.delivery.model.Pedido;
import com.delivery.model.Produto;
import com.delivery.model.Entrega;
import com.delivery.model.StatusEntrega;
import com.delivery.service.WebSocketService;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final String STATUS_AGUARDANDO_PAGAMENTO = "AGUARDANDO_PAGAMENTO";

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper pedidoMapper;
    private final WebSocketService webSocketService;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, PedidoMapper pedidoMapper, WebSocketService webSocketService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoMapper = pedidoMapper;
        this.webSocketService = webSocketService;
    }

    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO, Long clienteId) {
        Pedido pedido = new Pedido();
        pedido.setEnderecoPedido(pedidoRequestDTO.getEnderecoPedido());
        pedido.setCodigoCliente(clienteId);

        List<Produto> produtos = produtoRepository.findAllById(pedidoRequestDTO.getProdutoIds());
        if (produtos.size() != pedidoRequestDTO.getProdutoIds().size()) {
            throw new IllegalArgumentException("Um ou mais IDs de produtos são inválidos.");
        }
        pedido.setProdutos(produtos);

        float valorProdutos = (float) produtos.stream().mapToDouble(Produto::getPreco).sum();
        float taxaEntrega = 5.0f;
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(valorProdutos + taxaEntrega);

        pedido.setDataPedido(new Date());
        pedido.setStatus(STATUS_AGUARDANDO_PAGAMENTO);

        Entrega entrega = new Entrega();
        entrega.setStatus(StatusEntrega.PENDENTE);
        entrega.setPedido(pedido);
        pedido.setEntrega(entrega);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        webSocketService.notifyOrderUpdate(pedidoSalvo);
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    public List<PedidoResponseDTO> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByCodigoCliente(clienteId).stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: " + id)); // Idealmente, usar uma exceção customizada
    }

    public void excluir(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado com o ID: " + id); // Idealmente, usar uma exceção customizada
        }
        pedidoRepository.deleteById(id);
    }
}
