package com.delivery.dto;

import java.util.List;

public class PedidoRequestDTO {
    private String enderecoPedido;
    private List<Long> produtoIds;

    // Getters and Setters
    public String getEnderecoPedido() {
        return enderecoPedido;
    }

    public void setEnderecoPedido(String enderecoPedido) {
        this.enderecoPedido = enderecoPedido;
    }

    public List<Long> getProdutoIds() {
        return produtoIds;
    }

    public void setProdutoIds(List<Long> produtoIds) {
        this.produtoIds = produtoIds;
    }
}
