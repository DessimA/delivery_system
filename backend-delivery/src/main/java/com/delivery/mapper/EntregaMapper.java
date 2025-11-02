package com.delivery.mapper;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.model.Entrega;
import org.springframework.stereotype.Component;

@Component
public class EntregaMapper {

    public Entrega toEntity(EntregaRequestDTO dto) {
        Entrega entrega = new Entrega();
        entrega.setEnderecoOrigem(dto.getEnderecoOrigem());
        entrega.setEnderecoDestino(dto.getEnderecoDestino());
        entrega.setTempoEstimado(dto.getTempoEstimado());
        entrega.setValorEntrega(dto.getValorEntrega());
        return entrega;
    }

    public EntregaResponseDTO toResponseDTO(Entrega entrega) {
        EntregaResponseDTO dto = new EntregaResponseDTO();
        dto.setId(entrega.getId());
        dto.setStatus(entrega.getStatus().name());
        dto.setEnderecoOrigem(entrega.getEnderecoOrigem());
        dto.setEnderecoDestino(entrega.getEnderecoDestino());
        dto.setTempoEstimado(entrega.getTempoEstimado());
        dto.setValorEntrega(entrega.getValorEntrega());
        dto.setCriadoEm(entrega.getCriadoEm());
        dto.setEntregueEm(entrega.getEntregueEm());
        if (entrega.getPedido() != null) {
            dto.setCodigoPedido(entrega.getPedido().getCodigoPedido());
        }
        if (entrega.getEntregador() != null) {
            dto.setCodigoEntregador(entrega.getEntregador().getCodigo());
        }
        return dto;
    }
}
