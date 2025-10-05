package com.delivery.service;

import com.delivery.dto.PagamentoRequestDTO;
import com.delivery.dto.PagamentoResponseDTO;
import com.delivery.mapper.PagamentoMapper;
import com.delivery.model.Pagamento;
import com.delivery.model.Pedido;
import com.delivery.repository.PagamentoRepository;
import com.delivery.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PagamentoService {

    // Constants to avoid magic strings
    private static final String STATUS_APROVADO = "APROVADO";
    private static final String STATUS_RECUSADO = "RECUSADO";
    private static final String CARTAO_TESTE_RECUSADO = "1111";

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoMapper pagamentoMapper;

    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository, PagamentoMapper pagamentoMapper) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagamentoMapper = pagamentoMapper;
    }

    public PagamentoResponseDTO processarPagamento(PagamentoRequestDTO pagamentoRequestDTO) {
        Pedido pedido = pedidoRepository.findById(pagamentoRequestDTO.getCodigoPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getPagamento() != null) {
            throw new IllegalStateException("Este pedido já possui um pagamento associado.");
        }

        Pagamento pagamento = pagamentoMapper.toEntity(pagamentoRequestDTO);
        pagamento.setPedido(pedido);
        pagamento.setDataPagamento(new Date());

        // Em um cenário real, a lógica de gateway de pagamento seria mais complexa
        // e provavelmente estaria em um componente separado.
        boolean pagamentoAprovado = simularGatewayPagamento(pagamentoRequestDTO.getNumeroCartao());

        if (pagamentoAprovado) {
            pagamento.setStatus(STATUS_APROVADO);
            // TODO: Implementar a atualização do status do Pedido.
            // O status do pedido deve ser atualizado para refletir o pagamento aprovado
            // (ex: "AGUARDANDO_PREPARO" ou "PAGO").
        } else {
            pagamento.setStatus(STATUS_RECUSADO);
        }

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        return pagamentoMapper.toResponseDTO(pagamentoSalvo);
    }

    /**
     * Simula a chamada a um gateway de pagamento externo.
     * @param numeroCartao O número do cartão a ser processado.
     * @return {@code true} se o pagamento for aprovado, {@code false} caso contrário.
     */
    private boolean simularGatewayPagamento(String numeroCartao) {
        // Lógica de simulação simples: recusa um número de cartão específico.
        return !CARTAO_TESTE_RECUSADO.equals(numeroCartao);
    }
}
