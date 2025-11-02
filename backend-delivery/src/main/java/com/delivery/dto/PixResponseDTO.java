package com.delivery.dto;

import lombok.Data;

@Data
public class PixResponseDTO {
    private String qrCode;
    private String copiaCola;
    private String transactionId;
    private String expiraEm;
    private double valor;
}
