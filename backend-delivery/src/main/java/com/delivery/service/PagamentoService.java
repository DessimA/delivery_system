package com.delivery.service;

import com.delivery.model.Pagamento;
import com.delivery.model.Pedido;
import com.delivery.repository.PagamentoRepository;
import com.delivery.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public void criarPagamento(Long pedidoId, double valor, String transactionId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(valor);
        pagamento.setTransactionId(transactionId);
        pagamento.setStatus("PENDENTE");
        pagamento.setDataPagamento(new Date());

        pagamentoRepository.save(pagamento);
    }

    public String getStatusPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
        return pagamento.getStatus();
    }

    public void confirmarPagamento(String transactionId) {
        Pagamento pagamento = pagamentoRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));

        pagamento.setStatus("CONFIRMADO");
        pagamentoRepository.save(pagamento);

        Pedido pedido = pagamento.getPedido();
        pedido.setStatus("PAGO");
        pedidoRepository.save(pedido);
    }
}
