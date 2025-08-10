package com.delivery.service;

import com.delivery.dto.PagamentoRequestDTO;
import com.delivery.dto.PagamentoResponseDTO;
import com.delivery.mapper.PagamentoMapper;
import com.delivery.model.Pagamento;
import com.delivery.model.Pedido;
import com.delivery.repository.PagamentoRepository;
import com.delivery.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoMapper pagamentoMapper;

    public PagamentoResponseDTO processarPagamento(PagamentoRequestDTO pagamentoDTO) {
        Pedido pedido = pedidoRepository.findById(pagamentoDTO.getCodigoPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getPagamento() != null) {
            throw new IllegalStateException("Este pedido já possui um pagamento associado.");
        }

        Pagamento pagamento = pagamentoMapper.toEntity(pagamentoDTO);
        pagamento.setPedido(pedido);
        pagamento.setDataPagamento(new Date());

        // Simulação de processamento de pagamento
        // Em um cenário real, aqui haveria integração com um gateway de pagamento externo
        boolean pagamentoAprovado = simularGatewayPagamento(pagamentoDTO.getNumeroCartao());

        if (pagamentoAprovado) {
            pagamento.setStatus("APROVADO");
            // Atualizar status do pedido para PAGO ou similar
            // pedido.setStatus("PAGO");
            // pedidoRepository.save(pedido);
        } else {
            pagamento.setStatus("RECUSADO");
        }

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        return pagamentoMapper.toResponseDTO(pagamentoSalvo);
    }

    private boolean simularGatewayPagamento(String numeroCartao) {
        // Simulação simples: aprova se o número do cartão não for "1111" (exemplo de recusa)
        return !"1111".equals(numeroCartao);
    }
}
