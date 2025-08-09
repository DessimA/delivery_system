package com.delivery.mapper;

import com.delivery.dto.PedidoResponseDTO;
import com.delivery.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    @Autowired
    private ProdutoMapper produtoMapper;

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setCodigoPedido(pedido.getCodigoPedido());
        dto.setCodigoCliente(pedido.getCodigoCliente());
        dto.setEnderecoPedido(pedido.getEnderecoPedido());
        dto.setValorTotal(pedido.getValorTotal());
        if (pedido.getProdutos() != null) {
            dto.setProdutos(pedido.getProdutos().stream()
                    .map(produtoMapper::toResponseDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
