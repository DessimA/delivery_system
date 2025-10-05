package com.delivery.dto;

import java.util.List;

public class PedidoResponseDTO {
    private Long codigoPedido;
    private Long codigoCliente;
    private String enderecoPedido;
    private float valorTotal;
    private String status;
    private java.util.Date dataPedido;
    private List<ProdutoResponseDTO> produtos;

    // Getters and Setters
    public Long getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(Long codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public Long getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Long codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getEnderecoPedido() {
        return enderecoPedido;
    }

    public void setEnderecoPedido(String enderecoPedido) {
        this.enderecoPedido = enderecoPedido;
    }

    public float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ProdutoResponseDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoResponseDTO> produtos) {
        this.produtos = produtos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.util.Date getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(java.util.Date dataPedido) {
        this.dataPedido = dataPedido;
    }
}
