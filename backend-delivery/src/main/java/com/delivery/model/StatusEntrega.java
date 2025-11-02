package com.delivery.model;

public enum StatusEntrega {
    PENDENTE,           // Aguardando entregador
    ACEITA,             // Entregador aceitou
    COLETADA,           // Pedido coletado no restaurante
    EM_ROTA,            // A caminho do cliente
    ENTREGUE,           // Entrega concluída
    CANCELADA           // Cancelada
}
