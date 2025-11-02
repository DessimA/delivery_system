package com.delivery.service;

import com.delivery.model.Entrega;
import com.delivery.model.Pedido;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyDeliveryAvailable(Entrega entrega) {
        // Notifica entregadores sobre nova entrega disponível
        messagingTemplate.convertAndSend("/topic/entregas/disponiveis", entrega);
    }

    public void notifyDeliveryStatusUpdate(Entrega entrega) {
        // Notifica cliente e entregador sobre atualização de status da entrega
        if (entrega.getPedido() != null && entrega.getPedido().getCodigoPedido() != null) {
            messagingTemplate.convertAndSend("/topic/pedidos/" + entrega.getPedido().getCodigoPedido(), entrega);
        }
        if (entrega.getId() != null) {
            messagingTemplate.convertAndSend("/topic/entregas/" + entrega.getId(), entrega);
        }
    }

    public void notifyOrderUpdate(Pedido pedido) {
        // Notifica cliente sobre atualização de status do pedido
        if (pedido.getCodigoPedido() != null) {
            messagingTemplate.convertAndSend("/topic/pedidos/" + pedido.getCodigoPedido(), pedido);
        }
    }
}
