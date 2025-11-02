package com.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EstabelecimentoRequestDTO {
    @NotBlank(message = "O nome do estabelecimento é obrigatório.")
    private String nome;
    @NotBlank(message = "O endereço do estabelecimento é obrigatório.")
    private String endereco;
}
