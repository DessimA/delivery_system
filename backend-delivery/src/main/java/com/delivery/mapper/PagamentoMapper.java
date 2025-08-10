package com.delivery.mapper;

import com.delivery.dto.PagamentoRequestDTO;
import com.delivery.dto.PagamentoResponseDTO;
import com.delivery.model.Pagamento;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    public Pagamento toEntity(PagamentoRequestDTO dto) {
        Pagamento pagamento = new Pagamento();
        pagamento.setMetodoPagamento(dto.getMetodoPagamento());
        pagamento.setValor(dto.getValor());
        // Pedido e Status serão setados no serviço
        return pagamento;
    }

    public PagamentoResponseDTO toResponseDTO(Pagamento pagamento) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();
        dto.setId(pagamento.getId());
        dto.setMetodoPagamento(pagamento.getMetodoPagamento());
        dto.setValor(pagamento.getValor());
        dto.setStatus(pagamento.getStatus());
        dto.setDataPagamento(pagamento.getDataPagamento());
        if (pagamento.getPedido() != null) {
            dto.setCodigoPedido(pagamento.getPedido().getCodigoPedido());
        }
        return dto;
    }
}
