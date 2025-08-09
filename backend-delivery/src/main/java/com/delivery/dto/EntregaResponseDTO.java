package com.delivery.dto;

import java.math.BigDecimal;
import java.util.Date;

public class EntregaResponseDTO {
    private Long id;
    private Long codigoPedido;
    private Long codigoEntregador;
    private String status;
    private String enderecoOrigem;
    private String enderecoDestino;
    private Integer tempoEstimado;
    private BigDecimal valorEntrega;
    private Date criadoEm;
    private Date entregueEm;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(Long codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public Long getCodigoEntregador() {
        return codigoEntregador;
    }

    public void setCodigoEntregador(Long codigoEntregador) {
        this.codigoEntregador = codigoEntregador;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnderecoOrigem() {
        return enderecoOrigem;
    }

    public void setEnderecoOrigem(String enderecoOrigem) {
        this.enderecoOrigem = enderecoOrigem;
    }

    public String getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(String enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    public Integer getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(Integer tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public BigDecimal getValorEntrega() {
        return valorEntrega;
    }

    public void setValorEntrega(BigDecimal valorEntrega) {
        this.valorEntrega = valorEntrega;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(Date entregueEm) {
        this.entregueEm = entregueEm;
    }
}
