package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PedidoRequestDTO {
    @NotBlank(message = "O endereço de entrega é obrigatório.")
    private String enderecoPedido;
    @NotEmpty(message = "A lista de produtos não pode ser vazia.")
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
