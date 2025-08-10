package com.delivery.service;

import com.delivery.dto.PagamentoRequestDTO;
import com.delivery.model.Pagamento;
import com.delivery.model.Pedido;
import com.delivery.repository.PagamentoRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.mapper.PagamentoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PagamentoMapper pagamentoMapper;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pedido pedido;
    private PagamentoRequestDTO pagamentoRequestDTO;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setCodigoPedido(1L);
        pedido.setValorTotal(100.0f);

        pagamentoRequestDTO = new PagamentoRequestDTO();
        pagamentoRequestDTO.setCodigoPedido(1L);
        pagamentoRequestDTO.setMetodoPagamento("CREDIT_CARD");
        pagamentoRequestDTO.setValor(BigDecimal.valueOf(100.0));
        pagamentoRequestDTO.setNumeroCartao("1234567890123456");
    }

    @Test
    void processarPagamento_shouldApprovePaymentForValidCard() {
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.of(pedido));
        when(pagamentoMapper.toEntity(any(PagamentoRequestDTO.class))).thenReturn(new Pagamento());
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(new Pagamento());
        when(pagamentoMapper.toResponseDTO(any(Pagamento.class))).thenReturn(null); // Not testing DTO conversion here

        pagamentoService.processarPagamento(pagamentoRequestDTO);

        verify(pagamentoRepository, times(1)).save(argThat(pagamento ->
                "APROVADO".equals(pagamento.getStatus())
        ));
    }

    @Test
    void processarPagamento_shouldRejectPaymentForInvalidCard() {
        pagamentoRequestDTO.setNumeroCartao("1111"); // Simula cartão recusado

        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.of(pedido));
        when(pagamentoMapper.toEntity(any(PagamentoRequestDTO.class))).thenReturn(new Pagamento());
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(new Pagamento());
        when(pagamentoMapper.toResponseDTO(any(Pagamento.class))).thenReturn(null);

        pagamentoService.processarPagamento(pagamentoRequestDTO);

        verify(pagamentoRepository, times(1)).save(argThat(pagamento ->
                "RECUSADO".equals(pagamento.getStatus())
        ));
    }

    @Test
    void processarPagamento_shouldThrowExceptionIfPedidoNotFound() {
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                pagamentoService.processarPagamento(pagamentoRequestDTO)
        );
    }

    @Test
    void processarPagamento_shouldThrowExceptionIfPedidoAlreadyHasPayment() {
        pedido.setPagamento(new Pagamento()); // Simula pagamento existente
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class, () ->
                pagamentoService.processarPagamento(pagamentoRequestDTO)
        );
    }
}
