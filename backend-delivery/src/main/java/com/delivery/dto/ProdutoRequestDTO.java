package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProdutoRequestDTO {
    @NotBlank(message = "O nome do produto é obrigatório.")
    private String nomeProduto;
    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String descricao;
    @NotNull(message = "O preço do produto é obrigatório.")
    @Positive(message = "O preço deve ser um valor positivo.")
    private float preco;
    private Long estabelecimentoId;

    // Getters and Setters
    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public Long getEstabelecimentoId() {
        return estabelecimentoId;
    }

    public void setEstabelecimentoId(Long estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }
}
