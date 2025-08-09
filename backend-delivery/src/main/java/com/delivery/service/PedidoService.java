package com.delivery.service;

import com.delivery.dto.PedidoRequestDTO;
import com.delivery.dto.PedidoResponseDTO;
import com.delivery.mapper.PedidoMapper;
import com.delivery.model.Pedido;
import com.delivery.model.Produto;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoDTO, Long clienteId) {
        Pedido pedido = new Pedido();
        pedido.setEnderecoPedido(pedidoDTO.getEnderecoPedido());
        pedido.setCodigoCliente(clienteId);

        List<Produto> produtos = produtoRepository.findAllById(pedidoDTO.getProdutoIds());
        pedido.setProdutos(produtos);

        float valorTotal = (float) produtos.stream().mapToDouble(Produto::getPreco).sum();
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
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
                .orElse(null);
    }

    public void excluir(Long id) {
        pedidoRepository.deleteById(id);
    }
}
